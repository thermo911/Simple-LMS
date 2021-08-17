package com.blazhkov.demo.service.impl;

import com.blazhkov.demo.dao.CoverImageRepository;
import com.blazhkov.demo.domain.AvatarImage;
import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.domain.CoverImage;
import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.exception.CourseNotFoundException;
import com.blazhkov.demo.service.CourseService;
import com.blazhkov.demo.service.CoverImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.*;

@Service
public class CoverImageServiceImpl implements CoverImageService {

    private static final Logger logger
            = LoggerFactory.getLogger("CoverImageService");
    private final CourseService courseService;
    private final CoverImageRepository coverImageRepository;

    @Value("${file.storage.cover.path}")
    private String path;

    @Autowired
    public CoverImageServiceImpl(CourseService courseService,
                                 CoverImageRepository coverImageRepository) {
        this.courseService = courseService;
        this.coverImageRepository = coverImageRepository;
    }

    @Override
    public void save(Long courseId, String contentType, InputStream is) {
        Optional<CoverImage> opt = coverImageRepository.findByCourseId(courseId);
        CoverImage coverImage;
        String filename;

        if (opt.isEmpty()) {
            filename = UUID.randomUUID().toString();
            Course course = courseService.courseById(courseId)
                    .orElseThrow(CourseNotFoundException::new);
            coverImage = new CoverImage(null, contentType, filename, course);
        } else {
            coverImage = opt.get();
            filename = coverImage.getFilename();
            coverImage.setContentType(contentType);
        }
        coverImageRepository.save(coverImage);

        try (OutputStream os = Files.newOutputStream(
                Path.of(path, filename), CREATE, WRITE, TRUNCATE_EXISTING)
        ) {
            is.transferTo(os);
        } catch (IOException e) {
            logger.error("Can't write to file {}", filename, e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Optional<String> getContentTypeByCourseId(Long courseId) {
        return coverImageRepository.findByCourseId(courseId)
                .map(CoverImage::getContentType);
    }

    @Override
    public Optional<byte[]> getCoverImageByCourseId(Long courseId) {

        return coverImageRepository.findByCourseId(courseId)
                .map(CoverImage::getFilename)
                .map(filename -> {
                    try {
                        return Files.readAllBytes(Path.of(path, filename));
                    } catch (IOException e) {
                        logger.error("Can't read file {}", filename, e);
                        throw new IllegalStateException(e);
                    }
                });
    }
}

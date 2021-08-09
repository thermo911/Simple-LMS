package com.blazhkov.demo.service.impl;

import com.blazhkov.demo.dao.AvatarImageRepository;
import com.blazhkov.demo.domain.AvatarImage;
import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.service.AvatarImageService;
import com.blazhkov.demo.service.UserService;
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
public class AvatarImageServiceImpl implements AvatarImageService {

    private static final Logger logger = LoggerFactory.getLogger("AvatarImageService");
    private final AvatarImageRepository avatarImageRepository;
    private final UserService userService;

    @Value("${file.storage.path}")
    private String path;

    @Autowired
    public AvatarImageServiceImpl(AvatarImageRepository avatarImageRepository,
                                  UserService userService) {
        this.avatarImageRepository = avatarImageRepository;
        this.userService = userService;
    }

    @Override
    public void save(String username, String contentType, InputStream is) {

        Optional<AvatarImage> opt = avatarImageRepository.findByUsername(username);
        AvatarImage avatarImage;
        String filename;

        if (opt.isEmpty()) {
            filename = UUID.randomUUID().toString();
            User user = userService.userByUsername(username)
                    .orElseThrow(IllegalAccessError::new);
            avatarImage = new AvatarImage(null, contentType, filename, user);
        } else {
            avatarImage = opt.get();
            filename = avatarImage.getFilename();
            avatarImage.setContentType(contentType);
        }
        avatarImageRepository.save(avatarImage);

        try (OutputStream os = Files.newOutputStream(
                Path.of(path, filename), CREATE, WRITE, TRUNCATE_EXISTING)
        ) {
            is.transferTo(os);
        } catch (IOException e) {
            logger.error("Can't write to file {}", filename, e);
            throw new IllegalStateException(e);
        }
    }
}

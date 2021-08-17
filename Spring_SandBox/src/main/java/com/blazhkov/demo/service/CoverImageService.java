package com.blazhkov.demo.service;

import java.io.InputStream;
import java.util.Optional;

public interface CoverImageService {
    void save(Long courseId, String contentType, InputStream is);

    Optional<String> getContentTypeByCourseId(Long courseId);

    Optional<byte[]> getCoverImageByCourseId(Long courseId);
}

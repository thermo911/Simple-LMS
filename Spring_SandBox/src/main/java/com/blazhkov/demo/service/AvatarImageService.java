package com.blazhkov.demo.service;

import java.io.InputStream;

public interface AvatarImageService {

    void save(String username, String contentType, InputStream is);
}

package com.blazhkov.demo.controller;

import com.blazhkov.demo.exception.InternalServerError;
import com.blazhkov.demo.exception.NotFoundException;
import com.blazhkov.demo.service.AvatarImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger("Profile Controller");

    private final AvatarImageService avatarImageService;

    @Autowired
    public ProfileController(AvatarImageService avatarImageService) {
        this.avatarImageService = avatarImageService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String profilePage() {
        return "profile";
    }

    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public String updateAvatarImage(Authentication auth,
                                    @RequestParam("avatar") MultipartFile avatar) throws InternalServerError {
        logger.info("File name {}, file content type {}, file size {}",
                avatar.getOriginalFilename(), avatar.getContentType(), avatar.getSize());
        try {
            avatarImageService.save(auth.getName(), avatar.getContentType(), avatar.getInputStream());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerError();
        }
        return "redirect:/profile";
    }

    @GetMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<byte[]> avatarImage(Authentication auth) {
        String contentType = avatarImageService.getContentTypeByUsername(auth.getName())
                .orElseThrow(NotFoundException::new);
        byte[] data = avatarImageService.getAvatarImageByUsername(auth.getName())
                .orElseThrow(NotFoundException::new);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    @ExceptionHandler
    public ModelAndView internalServerErrorHandler(InternalServerError er) {
        ModelAndView modelAndView = new ModelAndView("server_error");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }

    @ExceptionHandler
    public ResponseEntity<Void> notFoundExceptionHandler(NotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}

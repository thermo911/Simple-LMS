package com.blazhkov.demo.controller;

import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.exception.InternalServerError;
import com.blazhkov.demo.exception.NotFoundException;
import com.blazhkov.demo.exception.ResourceNotFoundException;
import com.blazhkov.demo.exception.UserNotFoundException;
import com.blazhkov.demo.service.AvatarImageService;
import com.blazhkov.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.io.IOException;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger("Profile Controller");

    private final AvatarImageService avatarImageService;
    private final UserService userService;

    @Autowired
    public ProfileController(AvatarImageService avatarImageService,
                             UserService userService) {
        this.avatarImageService = avatarImageService;
        this.userService = userService;
    }

    @GetMapping("/{username}")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(@PathVariable("username") String username,
                              Authentication auth,
                              Model model) {
        User user = userService.userByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userId", user.getId());
        model.addAttribute("numOfCourses", user.getCourses().size());
        model.addAttribute("who", auth.getName().equals(username) ? "self" : "other");
        return "profile";
    }

    @PostMapping("/{username}/avatar")
    @PreAuthorize("isAuthenticated()")
    public String updateAvatarImage(@PathVariable("username") String username,
                                    Authentication auth,
                                    @RequestParam("avatar") MultipartFile avatar)
                                    throws InternalServerError {
        if (!auth.getName().equals(username))
            return String.format("redirect:/profile/%s", username);

        logger.info("File name {}, file content type {}, file size {}",
                avatar.getOriginalFilename(), avatar.getContentType(), avatar.getSize());
        try {
            avatarImageService.save(username, avatar.getContentType(), avatar.getInputStream());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerError();
        }
        return String.format("redirect:/profile/%s", username);
    }

    @GetMapping("/{username}/avatar")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<byte[]> avatarImage(@PathVariable("username") String username) {
        String contentType = avatarImageService.getContentTypeByUsername(username)
                .orElseThrow(ResourceNotFoundException::new);
        byte[] data = avatarImageService.getAvatarImageByUsername(username)
                .orElseThrow(ResourceNotFoundException::new);
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
    public ResponseEntity<Void> resourceNotFoundExceptionHandler(ResourceNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(UserNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("user_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }
}

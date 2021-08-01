package com.blazhkov.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {

    @RequestMapping("/access_denied")
    public String accessDenied() {
        return "access_denied";
    }
}

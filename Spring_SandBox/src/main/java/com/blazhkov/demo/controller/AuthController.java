package com.blazhkov.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {

    @RequestMapping("/access_denied")
    public ModelAndView accessDenied() {
        ModelAndView modelAndView = new ModelAndView("access_denied");
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        return modelAndView;
    }
}

package com.blazhkov.demo.controller;

import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String userTable(Model model) {
        model.addAttribute("users", userService.allUsers());
        model.addAttribute("activePage", "users");
        return "users";
    }

    @RequestMapping("/new")
    public String userForm(Model model) {
        model.addAttribute("user", new User());
        return "edit_user";
    }

    @PostMapping
    public String submitUserForm(@Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_user";
        }
        userService.saveUser(user);
        return "redirect:/user";
    }

 }

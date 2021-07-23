package com.blazhkov.demo.controller;

import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.exception.NotFoundException;
import com.blazhkov.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping("/{id}")
    public String userForm(Model model, @PathVariable(name = "id") Long id) {
        User user = userService.userById(id).orElseThrow(NotFoundException::new);
        model.addAttribute("user", user);
        return "edit_user";
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

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable(name = "id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/user";
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(NotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("user_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }


 }

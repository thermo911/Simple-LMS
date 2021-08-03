package com.blazhkov.demo.controller;

import com.blazhkov.demo.dao.RoleRepository;
import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.domain.Role;
import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.dto.UserDTO;
import com.blazhkov.demo.exception.UserNotFoundException;
import com.blazhkov.demo.service.CourseService;
import com.blazhkov.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/user")
public class UserController {
    UserService userService;
    CourseService courseService;
    RoleRepository roleRepository;

    @Autowired
    public UserController(UserService userService,
                          CourseService courseService,
                          RoleRepository roleRepository) {
        this.userService = userService;
        this.courseService = courseService;
        this.roleRepository = roleRepository;
    }

    @ModelAttribute("roles")
    public List<Role> rolesAttribute() {
        return roleRepository.findAll();
    }


    @GetMapping
    public String userTable(Model model) {
        model.addAttribute("users", userService.allUsers());
        model.addAttribute("activePage", "users");
        return "users";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping("/{id}")
    public String userForm(Model model, @PathVariable(name = "id") Long id) {
        UserDTO user = userService.userById(id, false).orElseThrow(UserNotFoundException::new);
        model.addAttribute("user", user);
        return "edit_user";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping("/new")
    public String userForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "edit_user";
    }

    @PostMapping
    public String submitUserForm(@Valid @ModelAttribute("user") UserDTO user,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_user";
        }
        userService.saveUser(user, false);
        return "redirect:/admin/user";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable(name = "id") Long id) {
        User user = User.fromDTO(userService.userById(id, false)
                .orElseThrow(UserNotFoundException::new));
        for (Course c: user.getCourses()) {
            c.getUsers().remove(user);
            courseService.saveCourse(c);
        }
        userService.deleteUserById(id);
        return "redirect:/admin/user";
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(UserNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("user_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }


 }

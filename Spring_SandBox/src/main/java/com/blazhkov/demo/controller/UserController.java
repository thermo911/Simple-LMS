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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/user")
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


    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public String userTable(Model model) {
        model.addAttribute("users", userService.allUsers());
        model.addAttribute("activePage", "users");
        return "users";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String userForm(Model model,
                           Authentication auth,
                           @PathVariable(name = "id") Long id) {
        UserDTO user = userService.userById(id, false).orElseThrow(UserNotFoundException::new);
        if (auth.getName().equals(user.getUsername()) ||
                auth.getAuthorities()
                        .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            model.addAttribute("user", user);
            return "edit_user";
        }
        return "redirect:/access_denied";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String userForm(Model model) {
        model.addAttribute("user",
                UserDTO
                .builder()
                .roles(Set.of(new Role(null, "ROLE_USER", null))));
        return "edit_user";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public String submitUserForm(@Valid @ModelAttribute("user") UserDTO user,
                                 BindingResult bindingResult,
                                 Authentication auth) {
        if (user.getUsername().equals(auth.getName())
            || auth.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
        if (bindingResult.hasErrors()) {
            return "edit_user";
        }
        userService.saveUser(user, false);
        return "redirect:/user";
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable(name = "id") Long id) {
        User user = User.fromDTO(userService.userById(id, false)
                .orElseThrow(UserNotFoundException::new));
        if (user.getCourses() != null) {
            for (Course c : user.getCourses()) {
                c.getUsers().remove(user);
                courseService.saveCourse(c);
            }
        }
        userService.deleteUserById(id);
        return "redirect:/user";
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(UserNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("user_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }
 }

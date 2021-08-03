package com.blazhkov.demo.controller;

import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.domain.User;
import com.blazhkov.demo.dto.LessonDTO;
import com.blazhkov.demo.dto.UserDTO;
import com.blazhkov.demo.exception.CourseNotFoundException;
import com.blazhkov.demo.exception.UserNotFoundException;
import com.blazhkov.demo.service.CourseService;
import com.blazhkov.demo.service.LessonService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collections;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;
    private final LessonService lessonService;
    private final UserService userService;

    @Autowired
    public CourseController(CourseService courseService,
                            LessonService lessonService,
                            UserService userService) {
        this.courseService = courseService;
        this.lessonService = lessonService;
        this.userService = userService;
    }

    /* Mappings */

    @GetMapping
    public String courseTable(Model model,
                              @RequestParam(name = "titlePrefix", required = false)
                              String titlePrefix, HttpSession session) {
        if (titlePrefix == null) titlePrefix = "";

        model.addAttribute("courses", courseService.coursesByTitleWithPrefix(titlePrefix + "%"));
        model.addAttribute("activePage", "courses");

        return "courses";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/{id}")
    public String courseForm(Model model, @PathVariable("id") Long id) {
        Course course = courseService.courseById(id).orElseThrow(CourseNotFoundException::new);

        model.addAttribute("course", course);
        model.addAttribute("activePage", "none");
        model.addAttribute("lessonDTOs",
                course.getLessons().stream().map(LessonDTO::new)
                .collect(Collectors.toList())
        );
        model.addAttribute("users", course.getUsers());

        return "edit_course";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public String submitCourseForm(@Valid Course course, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_course";
        }
        courseService.saveCourse(course);
        return "redirect:/course";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping("/new")
    public String courseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("activeRage", "none");
        return "edit_course";
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseService.removeCourse(id);
        return "redirect:/course";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/assign")
    public String assignUserForm(Model model,
                                 HttpServletRequest request,
                                 @PathVariable("id") Long id) {
        model.addAttribute("courseId", id);
        if (request.isUserInRole("ROLE_ADMIN")) {
            model.addAttribute("users", userService.usersNotAssignedToCourse(id));
        } else {
            User user = userService.userByUsername(request.getRemoteUser())
                    .orElseThrow(UserNotFoundException::new);
            model.addAttribute("users", Collections.singletonList(user));
        }
        return "assign_user";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/assign")
    public String submitAssignUserForm(@PathVariable(name="id") Long courseId,
                                       @RequestParam(name="userId") Long userId) {
        Course course = courseService.courseById(courseId)
                .orElseThrow(CourseNotFoundException::new);
        UserDTO user = userService.userById(userId, true)
                .orElseThrow(UserNotFoundException::new);
        course.getUsers().add(User.fromDTO(user));
        user.getCourses().add(course);
        courseService.saveCourse(course);
        userService.saveUser(user, true);
        return String.format("redirect:/course/%d", courseId);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/remove")
    public String removeUserFromCourse(@PathVariable(name="id") Long courseId,
                                       @RequestParam(name="userId") Long userId,
                                       Authentication auth) {
        String name = auth.getName();
        UserDTO user = userService.userById(userId, true)
                .orElseThrow(UserNotFoundException::new);

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
            || name.equals(user.getUsername())) {
            Course course = courseService.courseById(courseId)
                    .orElseThrow(CourseNotFoundException::new);
            user.getCourses().remove(course);
            course.getUsers().remove(User.fromDTO(user));
            courseService.saveCourse(course);
            userService.saveUser(user, true);
        }

        return String.format("redirect:/course/%d", courseId);
    }

    /* Exception handlers */

    @ExceptionHandler
    public ModelAndView courseNotFoundExceptionHandler(CourseNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("course_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView userNotFoundExceptionHandler(UserNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("user_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }
}
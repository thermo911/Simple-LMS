package com.blazhkov.demo.controller;

import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.exception.NotFoundException;
import com.blazhkov.demo.service.CourseCountUpdater;
import com.blazhkov.demo.service.CourseLister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping("/course")
public class CourseController {

    private final CourseLister courseLister;
    private final CourseCountUpdater courseCountUpdater;

    @Autowired
    public CourseController(CourseLister courseLister,
                            CourseCountUpdater courseCountUpdater) {
        this.courseLister = courseLister;
        this.courseCountUpdater = courseCountUpdater;
    }

    /* Mappings */

    @GetMapping
    public String courseTable(Model model,
                              @RequestParam(name = "titlePrefix", required = false)
                              String titlePrefix) {
        model.addAttribute("courses", courseLister.coursesByTitleWithPrefix(
                titlePrefix == null ? "" : titlePrefix
        ));
        model.addAttribute("activePage", "courses");
        return "course_table";
    }

    @RequestMapping("/{id}")
    public String courseForm(Model model, @PathVariable("id") Long id) {
        model.addAttribute("course", courseLister.courseById(id)
                .orElseThrow(NotFoundException::new));
        model.addAttribute("activePage", "none");
        return "course_form";
    }

    @PostMapping
    public String submitCourseForm(@Valid Course course, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "course_form";
        }
        courseCountUpdater.addCourse(course);
        return "redirect:/course";
    }

    @RequestMapping("/new")
    public String courseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("activeRage", "none");
        return "course_form";
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseCountUpdater.removeCourse(id);
        return "redirect:/course";
    }

    /* Exception handlers */

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(NotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("course_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }
}
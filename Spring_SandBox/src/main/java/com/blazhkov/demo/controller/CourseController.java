package com.blazhkov.demo.controller;

import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.dto.LessonDTO;
import com.blazhkov.demo.exception.NotFoundException;
import com.blazhkov.demo.service.CourseService;
import com.blazhkov.demo.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;
    private final LessonService lessonService;

    @Autowired
    public CourseController(CourseService courseService, LessonService lessonService) {
        this.courseService = courseService;
        this.lessonService = lessonService;
    }

    /* Mappings */

    @GetMapping
    public String courseTable(Model model,
                              @RequestParam(name = "titlePrefix", required = false)
                              String titlePrefix) {
        if (titlePrefix == null) titlePrefix = "";

        model.addAttribute("courses", courseService.coursesByTitleWithPrefix(titlePrefix + "%"));
        model.addAttribute("activePage", "courses");

        return "courses";
    }

    @RequestMapping("/{id}")
    public String courseForm(Model model, @PathVariable("id") Long id) {
        Course course = courseService.courseById(id).orElseThrow(NotFoundException::new);

        model.addAttribute("course", course);
        model.addAttribute("activePage", "none");
        model.addAttribute("lessonDTOs",
                course.getLessons().stream().map(LessonDTO::new)
                .collect(Collectors.toList())
        );

        return "edit_course";
    }

    @PostMapping
    public String submitCourseForm(@Valid Course course, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_course";
        }

        courseService.saveCourse(course);
        return "redirect:/course";
    }

    @RequestMapping("/new")
    public String courseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("activeRage", "none");
        return "edit_course";
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseService.removeCourse(id);
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
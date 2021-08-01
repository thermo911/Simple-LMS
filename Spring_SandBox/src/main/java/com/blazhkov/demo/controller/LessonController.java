package com.blazhkov.demo.controller;

import com.blazhkov.demo.domain.Lesson;
import com.blazhkov.demo.dto.LessonDTO;
import com.blazhkov.demo.exception.LessonNotFoundException;
import com.blazhkov.demo.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping("/lesson")
public class LessonController {
    private LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping("/new")
    public String lessonForm(Model model, @RequestParam("course_id") long courseId) {
        model.addAttribute("lessonDTO", new LessonDTO(courseId));
        return "edit_lesson";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public String submitLessonForm(@Valid LessonDTO lessonDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_lesson";
        }
        lessonService.saveLesson(lessonDTO);
        return String.format("redirect:/course/%d", lessonDTO.getCourseId());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/{id}")
    public String lessonForm(Model model, @PathVariable(name = "id") Long id) {
        Lesson lesson = lessonService.lessonById(id).orElseThrow(LessonNotFoundException::new);
        LessonDTO lessonDTO = new LessonDTO(lesson);
        model.addAttribute("lessonDTO", lessonDTO);
        return "edit_lesson";
    }


    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public String deleteLesson(@PathVariable(name = "id") Long id) {
        Lesson lesson = lessonService.lessonById(id).orElseThrow(LessonNotFoundException::new);
        Long courseId = lesson.getCourse().getId();
        lessonService.removeLesson(id);
        return String.format("redirect:/course/%d", courseId);
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(LessonNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("lesson_not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }
}

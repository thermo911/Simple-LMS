package com.blazhkov.demo.controller;

import com.blazhkov.demo.domain.Lesson;
import com.blazhkov.demo.dto.LessonDTO;
import com.blazhkov.demo.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/lesson")
public class LessonController {
    private LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @RequestMapping("/new")
    public String lessonForm(Model model, @RequestParam("course_id") long courseId) {
        //model.addAttribute("courseId", courseId);
        model.addAttribute("lesson", new LessonDTO(courseId));
        return "lesson_form";
    }

    @PostMapping
    public String submitLessonForm(@Valid LessonDTO lessonDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "lesson_form";
        }
        lessonService.saveLesson(lessonDto);
        return String.format("redirect:/course/%d", lessonDto.getCourseId());
    }

    @DeleteMapping("/{id}")
    public String deleteLesson(@PathVariable(name = "id") Long id) {
        Long courseId = lessonService.lessonById(id).get().getCourse().getId();
        lessonService.removeLesson(id);
        return String.format("redirect:/course/%d", courseId);
    }
}

package com.blazhkov.demo.service;

import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.domain.Lesson;
import com.blazhkov.demo.dto.LessonDTO;

import java.util.List;
import java.util.Optional;

public interface LessonService {

    Optional<Lesson> lessonById(Long id);

    List<Lesson> lessonsByCourse(Course course);

    void saveLesson(LessonDTO lessonDTO);

    void removeLesson(Long id);
}

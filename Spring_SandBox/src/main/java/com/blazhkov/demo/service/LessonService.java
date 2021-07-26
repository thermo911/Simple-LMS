package com.blazhkov.demo.service;

import com.blazhkov.demo.dao.CourseRepository;
import com.blazhkov.demo.dao.LessonRepository;
import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.domain.Lesson;
import com.blazhkov.demo.dto.LessonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Autowired
    private LessonService(LessonRepository lessonRepository, CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
    }

    public Optional<Lesson> lessonById(Long id) {
        return lessonRepository.findById(id);
    }

    public List<Lesson> lessonsByCourse(Course course) {
        return lessonRepository.findByCourse(course);
    }

    public void saveLesson(LessonDTO lessonDTO) {
        Course course = courseRepository.getById(lessonDTO.getCourseId());
        Lesson lesson = new Lesson(
                lessonDTO.getId(),
                lessonDTO.getTitle(),
                lessonDTO.getText(),
                course
        );
        lessonRepository.save(lesson);
    }

    public void removeLesson(Long id) {
        lessonRepository.deleteById(id);
    }
}
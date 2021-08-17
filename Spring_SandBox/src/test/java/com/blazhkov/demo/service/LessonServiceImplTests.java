package com.blazhkov.demo.service;

import com.blazhkov.demo.dao.CourseRepository;
import com.blazhkov.demo.dao.LessonRepository;
import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.domain.Lesson;
import com.blazhkov.demo.dto.LessonDTO;
import com.blazhkov.demo.service.impl.LessonServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
public class LessonServiceImplTests {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private LessonServiceImpl lessonService;

    private final Course c1 = new Course(1L, "Alex", "Course1", null, null, null);
    private final Course c2 = new Course(2L, "Dima", "Course2", null, null, null);

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LessonServiceImpl LessonServiceImpl(CourseRepository courseRepository,
                                 LessonRepository lessonRepository) {
            return new LessonServiceImpl(lessonRepository, courseRepository);
        }
    }

    @BeforeAll
    void setup() {
        courseRepository.saveAll(List.of(c1, c2));
        lessonRepository.save(new Lesson(1L, "Lesson1.1", null, c1));
    }

    @Test
    void lessonByIdTest() {
        Optional<Lesson> lesson = lessonService.lessonById(1L);
        assertTrue(lesson.isPresent());
        assertEquals("Lesson1.1", lesson.get().getTitle());
    }

    @Test
    void lessonByCourse() {
        List<Lesson> list = lessonService.lessonsByCourse(c1);
        assertEquals(1, list.size());
        assertEquals("Lesson1.1", list.get(0).getTitle());

        list = lessonService.lessonsByCourse(c2);
        assertTrue(list.isEmpty());
    }

    @Test
    void saveLessonTest() {
        int totalLessons = lessonRepository.findAll().size();

        Lesson l = new Lesson(3L, "Lesson2.1", null, c2);
        lessonService.saveLesson(new LessonDTO(l));

        assertEquals(totalLessons + 1, lessonRepository.findAll().size());
    }

    @Test
    void removeLessonTest() {
        int totalLessons = lessonRepository.findAll().size();
        lessonService.removeLesson(1L);
        assertEquals(totalLessons - 1, lessonRepository.findAll().size());
    }
}

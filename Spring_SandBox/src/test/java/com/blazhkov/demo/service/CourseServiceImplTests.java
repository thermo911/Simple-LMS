package com.blazhkov.demo.service;

import com.blazhkov.demo.dao.CourseRepository;
import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.service.impl.CourseServiceImpl;
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
public class CourseServiceImplTests {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseServiceImpl courseService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CourseServiceImpl CourseServiceImpl(CourseRepository courseRepository) {
            return new CourseServiceImpl(courseRepository);
        }
    }

    @BeforeAll
    void setup() {
        courseRepository.saveAll(List.of(
                new Course(1L, "Alex", "Course1", null, null),
                new Course(2L, "Dima", "Course2", null, null),
                new Course(3L, "John", "Course3", null, null),
                new Course(4L, "Lise", "NotCourse", null, null)
        ));
    }

    @Test
    void allCoursesTest() {
        List<Course> list = courseService.allCourses();
        assertEquals(4, list.size());
    }

    @Test
    void courseByIdTest() {
        Optional<Course> course = courseService.courseById(1L);
        assertTrue(course.isPresent());
        assertEquals("Alex", course.get().getAuthor());

        course = courseService.courseById(1000L);
        assertTrue(course.isEmpty());
    }

    @Test
    void courseByTitlePrefixTest() {

        // Note: only God knows why MySQL ignores prefix case and H2 does not...

        List<Course> list = courseService.coursesByTitleWithPrefix("Co");
        assertEquals(3, list.size());

        list = courseService.coursesByTitleWithPrefix("Not");
        assertEquals(1, list.size());

        list = courseService.coursesByTitleWithPrefix("LOL");
        assertTrue(list.isEmpty());
    }

    @Test
    void saveCourseTest() {
        int totalCourses = courseRepository.findAll().size();
        courseService.saveCourse(new Course(null, "Sally", "The art of not giving a fuck", null, null));
        assertEquals(totalCourses + 1, courseRepository.findAll().size());
    }

    @Test
    void removeCourseTest() {
        int totalCourses = courseRepository.findAll().size();
        courseService.removeCourse(1L);
        assertEquals(totalCourses - 1, courseRepository.findAll().size());
    }
}

package com.blazhkov.demo.service;

import com.blazhkov.demo.domain.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    List<Course> allCourses();

    List<Course> coursesByTitleWithPrefix(String prefix);

    Optional<Course> courseById(Long id);

    void saveCourse(Course course);

    void removeCourse(Long id);
}

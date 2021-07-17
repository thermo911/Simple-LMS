package com.blazhkov.demo.service;

import com.blazhkov.demo.dao.CourseRepository;
import com.blazhkov.demo.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class CourseCountUpdater {
    private final CourseRepository repository;

    @Autowired
    public CourseCountUpdater(CourseRepository repository) {
        this.repository = repository;
    }

    public void addCourse(Course course) {
        repository.save(course);
    }

    public void removeCourse(Long id) {
        repository.delete(id);
    }
}

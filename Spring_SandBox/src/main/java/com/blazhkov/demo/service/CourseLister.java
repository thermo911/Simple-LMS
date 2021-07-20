package com.blazhkov.demo.service;

import com.blazhkov.demo.dao.CourseRepository;
import com.blazhkov.demo.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseLister {
    private final CourseRepository repository;

    @Autowired
    public CourseLister(CourseRepository repository) {
        this.repository = repository;
    }

    public List<Course> allCourses() {
        return repository.findAll();
    }

    public List<Course> coursesByTitleWithPrefix(String prefix) {
        return repository.findByTitleLike(prefix);
    }

    public Optional<Course> courseById(Long id) {
        return repository.findById(id);
    }
}
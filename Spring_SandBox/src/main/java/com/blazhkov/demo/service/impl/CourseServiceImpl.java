package com.blazhkov.demo.service.impl;

import com.blazhkov.demo.dao.CourseRepository;
import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository repository;

    @Autowired
    public CourseServiceImpl(CourseRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Course> allCourses() {
        return repository.findAll();
    }

    @Override
    public List<Course> coursesByTitleWithPrefix(String prefix) {
        return repository.findByTitleLike(prefix + "%");
    }

    @Override
    public Optional<Course> courseById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void saveCourse(Course course) {
        repository.save(course);
    }

    @Override
    public void removeCourse(Long id) {
        repository.deleteById(id);
    }

}
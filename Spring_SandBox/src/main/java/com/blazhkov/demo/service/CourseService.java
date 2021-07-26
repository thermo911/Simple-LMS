package com.blazhkov.demo.service;

import com.blazhkov.demo.dao.CourseRepository;
import com.blazhkov.demo.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository repository;

    @Autowired
    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }

    // READ

    public List<Course> allCourses() {
        return repository.findAll();
    }

    public List<Course> coursesByTitleWithPrefix(String prefix) {
        return repository.findByTitleLike(prefix);
    }

    public Optional<Course> courseById(Long id) {
        return repository.findById(id);
    }

    // CREATE

    public void saveCourse(Course course) {
        repository.save(course);
    }

    // DELETE

    public void removeCourse(Long id) {
        repository.deleteById(id);
    }

}
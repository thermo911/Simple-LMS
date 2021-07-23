package com.blazhkov.demo.dao;

import com.blazhkov.demo.domain.Course;
import com.blazhkov.demo.domain.Lesson;
import com.blazhkov.demo.dto.LessonDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourse(Course course);

    @Query("select new com.blazhkov.demo.dto.LessonDTO(l.id, l.title, l.course.id) " +
            "from Lesson l where l.course.id = :id")
    List<LessonDTO> findAllForLessonIdWithoutText(@Param("id") long id);
}

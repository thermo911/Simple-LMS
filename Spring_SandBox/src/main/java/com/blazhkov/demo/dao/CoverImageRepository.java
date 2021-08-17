package com.blazhkov.demo.dao;

import com.blazhkov.demo.domain.CoverImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CoverImageRepository extends JpaRepository<CoverImage, Long> {
    @Query("FROM CoverImage ci WHERE ci.course.id = :courseId")
    Optional<CoverImage> findByCourseId(@Param("courseId") Long courseId);
}


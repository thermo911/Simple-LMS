package com.blazhkov.demo.dao;


import com.blazhkov.demo.domain.AvatarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AvatarImageRepository extends JpaRepository<AvatarImage, Long> {
    @Query("FROM AvatarImage ai WHERE ai.user.username = :username")
    Optional<AvatarImage> findByUsername(@Param("username") String username);
}

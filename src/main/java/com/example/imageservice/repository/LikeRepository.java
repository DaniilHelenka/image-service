package com.example.imageservice.repository;

import com.example.imageservice.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByImageIdAndUserId(Long imageId, Long userId);
}
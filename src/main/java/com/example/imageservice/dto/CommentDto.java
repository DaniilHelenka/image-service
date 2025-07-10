package com.example.imageservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentDto {
    private Long id;
    private Long imageId;
    private Long userId;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}
package com.example.imageservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ImageDto {
    private Long id;
    private Long userId;
    private String s3Url;
    private Instant uploadedAt;
}
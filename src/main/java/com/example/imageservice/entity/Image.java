package com.example.imageservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "s3_url", nullable = false)
    private String s3Url;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;
}
package com.example.imageservice.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_id", nullable = false)
    private Long imageId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
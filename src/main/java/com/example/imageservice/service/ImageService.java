package com.example.imageservice.service;

import com.example.imageservice.dto.CommentDto;
import com.example.imageservice.dto.CommentRequest;
import com.example.imageservice.dto.ImageDto;
import com.example.imageservice.entity.Comment;
import com.example.imageservice.entity.Image;
import com.example.imageservice.entity.Like;
import com.example.imageservice.repository.CommentRepository;
import com.example.imageservice.repository.ImageRepository;
import com.example.imageservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final S3Client s3Client;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.s3.endpoint}")
    private String endpoint;


    public ImageDto uploadImage(MultipartFile file, Long userId) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String s3Url = endpoint + "/" + bucketName + "/" + fileName;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to S3", e);
        }

        Image image = Image.builder()
                .userId(userId)
                .s3Url(s3Url)
                .uploadedAt(Instant.now())
                .build();

        image = imageRepository.save(image);
        return mapToImageDto(image);
    }

    public ImageDto getImageById(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return mapToImageDto(image);
    }

    public Page<ImageDto> getUserImages(Long userId, Pageable pageable) {
        return imageRepository.findByUserId(userId, pageable)
                .map(this::mapToImageDto);
    }

    public Page<ImageDto> getAllImages(Pageable pageable) {
        return imageRepository.findAll(pageable)
                .map(this::mapToImageDto);
    }

    public void toggleLike(Long imageId, Long userId) {
        imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Like existingLike = likeRepository.findByImageIdAndUserId(imageId, userId)
                .orElse(null);

        if (existingLike != null) {
            likeRepository.delete(existingLike);
        } else {
            Like like = Like.builder()
                    .imageId(imageId)
                    .userId(userId)
                    .build();
            likeRepository.save(like);
        }
    }

    public CommentDto addComment(Long imageId, Long userId, CommentRequest request) {
        imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Comment comment = Comment.builder()
                .imageId(imageId)
                .userId(userId)
                .content(request.getContent())
                .createdAt(Instant.now())
                .build();

        comment = commentRepository.save(comment);
        return mapToCommentDto(comment);
    }

    public void deleteComment(Long imageId, Long commentId, Long userId) {
        imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete comment");
        }

        commentRepository.delete(comment);
    }

    public CommentDto updateComment(Long imageId, Long commentId, Long userId, CommentRequest request) {
        imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update comment");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(Instant.now());
        comment = commentRepository.save(comment);
        return mapToCommentDto(comment);
    }

    private ImageDto mapToImageDto(Image image) {
        ImageDto dto = new ImageDto();
        dto.setId(image.getId());
        dto.setUserId(image.getUserId());
        dto.setS3Url(image.getS3Url());
        dto.setUploadedAt(image.getUploadedAt());
        return dto;
    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setImageId(comment.getImageId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
}
package com.example.imageservice.controller;


import com.example.imageservice.dto.CommentDto;
import com.example.imageservice.dto.CommentRequest;
import com.example.imageservice.dto.ImageDto;
import com.example.imageservice.service.ActivityProducer;
import com.example.imageservice.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final ActivityProducer activityProducer; // Inject Kafka producer

    @PostMapping("/api/images")
    public ImageDto uploadImage(@RequestParam("file") MultipartFile file,
                                @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getClaim("userId").toString());
        ImageDto imageDto = imageService.uploadImage(file, userId);
        activityProducer.sendActivity("uploadImage", userId, imageDto.getId());
        return imageDto;
    }

    @GetMapping("/api/images/{id}")
    public ImageDto getImageById(@PathVariable Long id) {
        return imageService.getImageById(id);
    }

    @GetMapping("/api/user/{id}/images")
    public Page<ImageDto> getUserImages(@PathVariable Long id,
                                        @PageableDefault(size = 10) Pageable pageable) {
        return imageService.getUserImages(id, pageable);
    }

    @GetMapping("/api/images")
    public Page<ImageDto> getAllImages(@PageableDefault(size = 10) Pageable pageable) {
        return imageService.getAllImages(pageable);
    }

    @PostMapping("/api/images/{id}/likes")
    public void toggleLike(@PathVariable Long id,
                           @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getClaim("userId").toString());
        imageService.toggleLike(id, userId);
        activityProducer.sendActivity("toggleLike", userId, id);
    }

    @PostMapping("/api/images/{id}/comments")
    public CommentDto addComment(@PathVariable Long id,
                                 @Valid @RequestBody CommentRequest request,
                                 @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getClaim("userId").toString());
        CommentDto commentDto = imageService.addComment(id, userId, request);
        activityProducer.sendActivity("addComment", userId, id);
        return commentDto;
    }

    @DeleteMapping("/api/images/{id}/comments/{commentId}")
    public void deleteComment(@PathVariable Long id,
                              @PathVariable Long commentId,
                              @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getClaim("userId").toString());
        imageService.deleteComment(id, commentId, userId);
        activityProducer.sendActivity("deleteComment", userId, id);
    }

    @PutMapping("/api/images/{id}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long id,
                                    @PathVariable Long commentId,
                                    @Valid @RequestBody CommentRequest request,
                                    @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getClaim("userId").toString());
        CommentDto commentDto = imageService.updateComment(id, commentId, userId, request);
        activityProducer.sendActivity("updateComment", userId, id);
        return commentDto;
    }
}
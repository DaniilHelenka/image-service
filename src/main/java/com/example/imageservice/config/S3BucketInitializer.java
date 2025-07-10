package com.example.imageservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3BucketInitializer {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @PostConstruct
    public void createBucketIfNotExists() {
        try {
            boolean exists = s3Client.listBuckets().buckets().stream()
                    .anyMatch(bucket -> bucket.name().equals(bucketName));

            if (!exists) {
                s3Client.createBucket(CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build());

                log.info("✅ S3 bucket '{}' created successfully.", bucketName);
            } else {
                log.info("ℹ️ S3 bucket '{}' already exists.", bucketName);
            }
        } catch (S3Exception e) {
            log.error("❌ Failed to create/check S3 bucket '{}': {}", bucketName, e.awsErrorDetails().errorMessage());
        }
    }
}
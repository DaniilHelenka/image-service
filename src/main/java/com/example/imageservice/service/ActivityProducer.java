package com.example.imageservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActivityProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendActivity(String action, Long userId, Long imageId) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("userId", userId);
        message.put("imageId", imageId);
        kafkaTemplate.send("activity-topic", message);
    }
}

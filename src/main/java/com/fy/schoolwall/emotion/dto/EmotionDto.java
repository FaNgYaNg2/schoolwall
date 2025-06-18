package com.fy.schoolwall.emotion.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class EmotionDto {
    private Long id;
    private Long postId;
    private Long commentId;
    private String text;
    private String sentiment;
    private Double confidence;
    private Map<String, Double> probabilities;
    private LocalDateTime createdAt;
}
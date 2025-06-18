package com.fy.schoolwall.emotion.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Emotion {
    private Long id;
    private Long postId;
    private Long commentId;
    private String text;
    private String sentiment;
    private Double confidence;
    private String probabilitiesJson;
    private LocalDateTime createdAt;
}

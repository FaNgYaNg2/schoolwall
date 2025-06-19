package com.fy.schoolwall.user.dto;

import lombok.Data;
import java.util.Map;

@Data
public class UserEmotionStatsDto {
    private Long userId;
    private Map<String, Long> postEmotionCounts;
    private Map<String, Long> commentEmotionCounts;
    private Map<String, Long> totalEmotionCounts;
}
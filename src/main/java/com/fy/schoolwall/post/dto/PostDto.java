package com.fy.schoolwall.post.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String slug;
    private Long authorId;
    private String authorUsername;
    private String status;
    private String category;
    private String tags;
    private String coverImage;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isTop;
    private Boolean isRecommended;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}
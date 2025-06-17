package com.fy.schoolwall.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    
    @NotNull(message = "Post ID is required")
    private Long postId;
    
    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 1000, message = "Comment content must be between 1 and 1000 characters")
    private String content;
    
    private Long parentCommentId; // 可选，用于回复评论
}
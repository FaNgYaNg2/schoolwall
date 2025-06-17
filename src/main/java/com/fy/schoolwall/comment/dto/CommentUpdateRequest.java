package com.fy.schoolwall.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentUpdateRequest {
    
    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 1000, message = "Comment content must be between 1 and 1000 characters")
    private String content;
}
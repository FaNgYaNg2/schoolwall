package com.fy.schoolwall.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content cannot exceed 10000 characters")
    private String content;

    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    @Size(max = 200, message = "Tags cannot exceed 200 characters")
    private String tags;

    @Size(max = 255, message = "Cover image URL cannot exceed 255 characters")
    private String coverImage;

    private String status = "DRAFT"; // 默认为草稿状态
}
package com.fy.schoolwall.post.dto;

import com.fy.schoolwall.common.enums.PostCategory;
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

    /**
     * 验证分类是否有效
     */
    public boolean isValidCategory() {
        return category == null || category.trim().isEmpty() || PostCategory.isValid(category);
    }

    /**
     * 获取分类枚举
     * 如果分类无效，抛出异常
     */
    public PostCategory getCategoryEnum() {
        if (category == null || category.trim().isEmpty()) {
            return null;
        }
        
        PostCategory categoryEnum = PostCategory.fromCode(category);
        if (categoryEnum == null) {
            // 尝试通过显示名称匹配
            categoryEnum = PostCategory.fromDisplayName(category);
        }
        
        if (categoryEnum == null) {
            throw new IllegalArgumentException("Invalid post category: " + category + 
                ". Please use a valid category code or display name.");
        }
        
        return categoryEnum;
    }

    /**
     * 获取分类的显示名称
     */
    public String getCategoryDisplayName() {
        PostCategory categoryEnum = getCategoryEnum();
        return categoryEnum != null ? categoryEnum.getDisplayName() : category;
    }
}
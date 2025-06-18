package com.fy.schoolwall.post.dto;

import com.fy.schoolwall.common.enums.PostCategory;
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
    private String categoryDisplayName; // 分类显示名称
    private String tags;
    private String coverImage;
    private Integer viewCount;
    private Integer commentCount;
    private Boolean isTop;
    private Boolean isRecommended;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    /**
     * 设置分类并自动设置显示名称
     */
    public void setCategory(String category) {
        this.category = category;
        PostCategory categoryEnum = PostCategory.fromCode(category);
        this.categoryDisplayName = categoryEnum != null ? categoryEnum.getDisplayName() : category;
    }

    /**
     * 设置分类显示名称
     */
    public void setCategoryDisplayName(String categoryDisplayName) {
        this.categoryDisplayName = categoryDisplayName;
    }

    /**
     * 获取分类枚举
     */
    public PostCategory getCategoryEnum() {
        return PostCategory.fromCode(this.category);
    }

    /**
     * 验证分类是否有效
     */
    public boolean isValidCategory() {
        return PostCategory.isValid(this.category);
    }
}
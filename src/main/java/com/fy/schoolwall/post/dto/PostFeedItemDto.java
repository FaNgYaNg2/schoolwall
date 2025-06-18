package com.fy.schoolwall.post.dto;

import com.fy.schoolwall.common.enums.PostCategory;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostFeedItemDto {
    private Long id;
    private String title;
    private String summary; // 内容摘要
    private String slug;
    private String authorUsername;
    private String category;
    private String categoryDisplayName; // 分类显示名称
    private String coverImage;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isTop;
    private Boolean isRecommended;
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
     * 生成内容摘要
     */
    public void generateSummary(String content) {
        if (content != null && content.length() > 150) {
            this.summary = content.substring(0, 150) + "...";
        } else {
            this.summary = content;
        }
    }
}
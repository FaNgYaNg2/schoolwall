package com.fy.schoolwall.post.model;

import com.fy.schoolwall.common.util.SlugGenerator;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Post {
    private Long id;
    private String title;
    private String content;
    private String slug; // URL友好的标识符
    private Long authorId; // 作者ID
    private String authorUsername; // 作者用户名（冗余字段，方便查询）
    private String status; // 状态：DRAFT, PUBLISHED, HIDDEN, DELETED
    private String category; // 分类
    private String tags; // 标签，用逗号分隔
    private String coverImage; // 封面图片URL
    private Integer viewCount; // 浏览次数
    private Integer commentCount; // 评论数
    private Boolean isTop; // 是否置顶
    private Boolean isRecommended; // 是否推荐
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
    private LocalDateTime publishedAt; // 发布时间

    /**
     * 生成帖子slug
     */
    public void generateSlug() {
        if (this.title != null && !this.title.trim().isEmpty()) {
            this.slug = SlugGenerator.generateSlug(this.title);
        }
    }

    /**
     * 生成唯一slug
     */
    public void generateUniqueSlug() {
        if (this.title != null && !this.title.trim().isEmpty()) {
            this.slug = SlugGenerator.generateUniqueSlug(this.title);
        }
    }

    /**
     * 检查是否已发布
     */
    public boolean isPublished() {
        return "PUBLISHED".equals(this.status);
    }

    /**
     * 检查是否为草稿
     */
    public boolean isDraft() {
        return "DRAFT".equals(this.status);
    }
}
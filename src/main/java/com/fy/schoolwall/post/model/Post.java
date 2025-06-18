package com.fy.schoolwall.post.model;

import com.fy.schoolwall.common.util.SlugGenerator;
import com.fy.schoolwall.common.enums.PostCategory;
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
    private String category; // 分类代码
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

    /**
     * 获取分类枚举
     */
    public PostCategory getCategoryEnum() {
        return PostCategory.fromCode(this.category);
    }

    /**
     * 设置分类（通过枚举）
     */
    public void setCategory(PostCategory category) {
        this.category = category != null ? category.getCode() : null;
    }

    /**
     * 设置分类（通过字符串代码）
     */
    public void setCategory(String categoryCode) {
        if (categoryCode == null || categoryCode.trim().isEmpty()) {
            this.category = null;
            return;
        }
        
        // 验证分类代码是否有效
        PostCategory categoryEnum = PostCategory.fromCode(categoryCode);
        if (categoryEnum != null) {
            this.category = categoryEnum.getCode();
        } else {
            // 尝试通过显示名称匹配
            categoryEnum = PostCategory.fromDisplayName(categoryCode);
            if (categoryEnum != null) {
                this.category = categoryEnum.getCode();
            } else {
                throw new IllegalArgumentException("Invalid post category: " + categoryCode);
            }
        }
    }

    /**
     * 获取分类显示名称
     */
    public String getCategoryDisplayName() {
        PostCategory categoryEnum = getCategoryEnum();
        return categoryEnum != null ? categoryEnum.getDisplayName() : this.category;
    }

    /**
     * 验证分类是否有效
     */
    public boolean isValidCategory() {
        return PostCategory.isValid(this.category);
    }
}
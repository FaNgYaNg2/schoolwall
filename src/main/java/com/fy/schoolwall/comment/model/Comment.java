package com.fy.schoolwall.comment.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private String content;
    private Long userId;
    private Long postId;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    
    // 冗余字段，方便查询和显示
    private String username;
    private String userAvatarUrl;
    private String postTitle;
    private String parentCommentContent;
    
    // 新增字段，提高功能完整性
    private String ipAddress;           // IP地址，用于防刷和统计
    private Boolean isActive;           // 是否激活，用于管理员控制
    private String deleteReason;       // 删除原因，管理员删除时记录
    private Long deletedBy;            // 删除操作者ID（用户自己删除或管理员删除）
    private LocalDateTime deletedAt;   // 删除时间
    
    /**
     * 检查是否为顶级评论
     */
    public boolean isTopLevel() {
        return parentCommentId == null;
    }
    
    /**
     * 检查是否为回复评论
     */
    public boolean isReply() {
        return parentCommentId != null;
    }
    
    /**
     * 检查是否已删除
     */
    public boolean isDeleted() {
        return isDeleted != null && isDeleted;
    }
    
    /**
     * 检查是否激活
     */
    public boolean isActive() {
        return isActive == null || isActive;
    }
    
    /**
     * 检查是否可见（未删除且激活）
     */
    public boolean isVisible() {
        return !isDeleted() && isActive();
    }
    
    /**
     * 获取显示内容（如果已删除则显示占位文本）
     */
    public String getDisplayContent() {
        if (isDeleted()) {
            return "[此评论已被删除]";
        }
        if (!isActive()) {
            return "[此评论已被隐藏]";
        }
        return content;
    }
    
    /**
     * 获取内容预览（截断长内容）
     */
    public String getContentPreview(int maxLength) {
        String displayContent = getDisplayContent();
        if (displayContent.length() <= maxLength) {
            return displayContent;
        }
        return displayContent.substring(0, maxLength) + "...";
    }
    
    /**
     * 获取默认长度的内容预览
     */
    public String getContentPreview() {
        return getContentPreview(100);
    }
    
    /**
     * 获取评论层级（用于嵌套显示）
     */
    public int getLevel() {
        return isTopLevel() ? 0 : 1;
    }
    
    /**
     * 检查是否为最近评论（24小时内）
     */
    public boolean isRecent() {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }
    
    /**
     * 检查是否被编辑过
     */
    public boolean isEdited() {
        if (createdAt == null || updatedAt == null) {
            return false;
        }
        // 如果更新时间比创建时间晚超过1分钟，认为被编辑过
        return updatedAt.isAfter(createdAt.plusMinutes(1));
    }
    
    /**
     * 获取父评论内容预览
     */
    public String getParentContentPreview() {
        if (parentCommentContent == null || parentCommentContent.trim().isEmpty()) {
            return null;
        }
        return getContentPreview(parentCommentContent, 50);
    }
    
    /**
     * 辅助方法：获取指定内容的预览
     */
    private String getContentPreview(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
    
    /**
     * 设置删除状态
     */
    public void markAsDeleted(Long deletedBy, String reason) {
        this.isDeleted = true;
        this.deletedBy = deletedBy;
        this.deleteReason = reason;
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 设置非激活状态
     */
    public void markAsInactive(String reason) {
        this.isActive = false;
        this.deleteReason = reason;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 恢复激活状态
     */
    public void markAsActive() {
        this.isActive = true;
        this.deleteReason = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 获取删除类型描述
     */
    public String getDeleteTypeDescription() {
        if (!isDeleted()) {
            return null;
        }
        // 这里可以根据deletedBy和其他字段判断删除类型
        return deleteReason != null ? deleteReason : "用户删除";
    }
    
    /**
     * 检查内容是否过长
     */
    public boolean isContentLong(int threshold) {
        return content != null && content.length() > threshold;
    }
    
    /**
     * 获取评论状态描述
     */
    public String getStatusDescription() {
        if (isDeleted()) {
            return "已删除";
        }
        if (!isActive()) {
            return "已隐藏";
        }
        return "正常";
    }
}
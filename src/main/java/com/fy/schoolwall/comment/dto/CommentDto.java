package com.fy.schoolwall.comment.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {
    
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String userAvatarUrl;
    private Long postId;
    private String postTitle;
    private Long parentCommentId;
    private String parentCommentContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private Boolean isTopLevel;
    private Boolean isReply;
    private Boolean canEdit;
    
    // 回复相关字段
    private List<CommentDto> replies;
    private Long replyCount;
    
    // 显示相关字段
    private String timeAgo; // 相对时间显示，如 "2小时前"
    private String formattedCreatedAt; // 格式化的创建时间
    private String formattedUpdatedAt; // 格式化的更新时间
    
    /**
     * 检查是否为顶级评论
     */
    public boolean isTopLevel() {
        return isTopLevel != null && isTopLevel;
    }
    
    /**
     * 检查是否为回复评论
     */
    public boolean isReply() {
        return isReply != null && isReply;
    }
    
    /**
     * 检查是否已删除
     */
    public boolean isDeleted() {
        return isDeleted != null && isDeleted;
    }
    
    /**
     * 检查是否可以编辑
     */
    public boolean canEdit() {
        return canEdit != null && canEdit;
    }
    
    /**
     * 检查是否有回复
     */
    public boolean hasReplies() {
        return replyCount != null && replyCount > 0;
    }
    
    /**
     * 获取回复数量
     */
    public long getReplyCount() {
        return replyCount != null ? replyCount : 0;
    }
    
    /**
     * 获取显示用的回复计数文本
     */
    public String getReplyCountDisplay() {
        long count = getReplyCount();
        if (count == 0) {
            return "回复";
        } else if (count == 1) {
            return "1条回复";
        } else {
            return count + "条回复";
        }
    }
    
    /**
     * 获取评论层级（用于前端缩进显示）
     */
    public int getLevel() {
        return isTopLevel() ? 0 : 1;
    }
    
    /**
     * 获取评论类型描述
     */
    public String getCommentType() {
        return isTopLevel() ? "评论" : "回复";
    }
    
    /**
     * 获取父评论的简短内容（用于显示回复的上下文）
     */
    public String getParentCommentPreview() {
        if (parentCommentContent == null || parentCommentContent.trim().isEmpty()) {
            return null;
        }
        if (parentCommentContent.length() <= 30) {
            return parentCommentContent;
        }
        return parentCommentContent.substring(0, 30) + "...";
    }
    
    /**
     * 检查内容是否超过指定长度
     */
    public boolean isContentLong(int maxLength) {
        return content != null && content.length() > maxLength;
    }
    
    /**
     * 获取内容预览（截断长内容）
     */
    public String getContentPreview(int maxLength) {
        if (content == null) {
            return "";
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
    
    /**
     * 获取默认长度的内容预览
     */
    public String getContentPreview() {
        return getContentPreview(100);
    }
}
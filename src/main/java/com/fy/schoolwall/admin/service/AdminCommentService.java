package com.fy.schoolwall.admin.service;

import com.fy.schoolwall.comment.dto.CommentDto;
import com.fy.schoolwall.comment.model.Comment;
import com.fy.schoolwall.comment.repository.CommentMapper;
import com.fy.schoolwall.common.enums.UserRole;
import com.fy.schoolwall.common.util.PaginationUtil;
import com.fy.schoolwall.user.model.User;
import com.fy.schoolwall.user.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminCommentService {

    private final CommentMapper commentMapper;
    private final UserService userService;

    public AdminCommentService(CommentMapper commentMapper, UserService userService) {
        this.commentMapper = commentMapper;
        this.userService = userService;
    }

    /**
     * 验证管理员权限
     */
    private void validateAdminAccess() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        userService.validateUserRole(currentUser, UserRole.ADMIN);
    }

    /**
     * 获取所有评论（分页）
     */
    public PaginationUtil.PageResponse<CommentDto> getAllComments(PaginationUtil.PageRequest pageRequest) {
        validateAdminAccess();

        List<Comment> comments = commentMapper.findAllComments(pageRequest.getOffset(), pageRequest.getLimit());
        List<CommentDto> commentDtos = comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());

        long totalElements = commentMapper.countAllComments();
        return PaginationUtil.createPageResponse(commentDtos, pageRequest, totalElements);
    }

    /**
     * 根据删除状态获取评论
     */
    public PaginationUtil.PageResponse<CommentDto> getCommentsByDeletedStatus(Boolean isDeleted,
            PaginationUtil.PageRequest pageRequest) {
        validateAdminAccess();

        List<Comment> comments = commentMapper.findCommentsByDeletedStatus(isDeleted, pageRequest.getOffset(),
                pageRequest.getLimit());
        List<CommentDto> commentDtos = comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());

        long totalElements = commentMapper.countCommentsByDeletedStatus(isDeleted);
        return PaginationUtil.createPageResponse(commentDtos, pageRequest, totalElements);
    }

    /**
     * 获取用户的评论（管理员视角）
     */
    public PaginationUtil.PageResponse<CommentDto> getUserComments(Long userId,
            PaginationUtil.PageRequest pageRequest) {
        validateAdminAccess();

        // 验证用户存在
        userService.getUserById(userId);

        List<Comment> comments = commentMapper.findByUserId(userId, pageRequest.getOffset(), pageRequest.getLimit());
        List<CommentDto> commentDtos = comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());

        long totalElements = commentMapper.countByUserId(userId);
        return PaginationUtil.createPageResponse(commentDtos, pageRequest, totalElements);
    }

    /**
     * 获取帖子的评论（管理员视角）
     */
    public PaginationUtil.PageResponse<CommentDto> getPostComments(Long postId,
            PaginationUtil.PageRequest pageRequest) {
        validateAdminAccess();

        List<Comment> comments = commentMapper.findByPostId(postId, pageRequest.getOffset(), pageRequest.getLimit());
        List<CommentDto> commentDtos = comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());

        long totalElements = commentMapper.countByPostId(postId);
        return PaginationUtil.createPageResponse(commentDtos, pageRequest, totalElements);
    }

    /**
     * 管理员软删除评论
     */
    @Transactional
    public void softDeleteComment(Long commentId) {
        validateAdminAccess();

        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found with ID: " + commentId);
        }

        if (comment.isDeleted()) {
            throw new RuntimeException("Comment already deleted");
        }

        commentMapper.softDeleteById(commentId);

        User currentUser = userService.getCurrentAuthenticatedUser();
        System.out.println("Comment soft deleted by admin. Comment ID: " + commentId +
                ", Admin ID: " + currentUser.getId());
    }

    /**
     * 管理员物理删除评论
     */
    @Transactional
    public void deleteComment(Long commentId) {
        validateAdminAccess();

        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found with ID: " + commentId);
        }

        commentMapper.deleteById(commentId);

        User currentUser = userService.getCurrentAuthenticatedUser();
        System.out.println("Comment permanently deleted by admin. Comment ID: " + commentId +
                ", Admin ID: " + currentUser.getId());
    }

    /**
     * 批量软删除评论
     */
    @Transactional
    public void batchSoftDeleteComments(List<Long> commentIds) {
        validateAdminAccess();

        User currentUser = userService.getCurrentAuthenticatedUser();

        commentMapper.batchSoftDelete(commentIds);

        System.out.println("Batch soft deleted " + commentIds.size() + " comments by admin ID: " + currentUser.getId());
    }

    /**
     * 批量物理删除评论
     */
    @Transactional
    public void batchDeleteComments(List<Long> commentIds) {
        validateAdminAccess();

        User currentUser = userService.getCurrentAuthenticatedUser();

        commentMapper.batchDelete(commentIds);

        System.out.println(
                "Batch permanently deleted " + commentIds.size() + " comments by admin ID: " + currentUser.getId());
    }

    /**
     * 转换为CommentDto
     */
    private CommentDto convertToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent()); // 管理员可以看到原始内容
        dto.setUserId(comment.getUserId());
        dto.setUsername(comment.getUsername());
        dto.setUserAvatarUrl(comment.getUserAvatarUrl());
        dto.setPostId(comment.getPostId());
        dto.setPostTitle(comment.getPostTitle());
        dto.setParentCommentId(comment.getParentCommentId());
        dto.setParentCommentContent(comment.getParentCommentContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setIsDeleted(comment.isDeleted());
        dto.setIsTopLevel(comment.isTopLevel());
        dto.setIsReply(comment.isReply());
        dto.setCanEdit(false); // 管理员不能编辑用户评论内容

        return dto;
    }
}
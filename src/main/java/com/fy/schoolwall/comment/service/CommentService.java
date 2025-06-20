package com.fy.schoolwall.comment.service;

import com.fy.schoolwall.comment.dto.CommentDto;
import com.fy.schoolwall.comment.dto.CommentRequest;
import com.fy.schoolwall.comment.dto.CommentUpdateRequest;
import com.fy.schoolwall.comment.model.Comment;
import com.fy.schoolwall.comment.repository.CommentMapper;
import com.fy.schoolwall.common.exception.ResourceNotFoundException;
import com.fy.schoolwall.common.util.PaginationUtil;
import com.fy.schoolwall.post.model.Post;
import com.fy.schoolwall.post.repository.PostMapper;
import com.fy.schoolwall.user.model.User;
import com.fy.schoolwall.user.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final UserService userService;

    public CommentService(CommentMapper commentMapper, PostMapper postMapper, UserService userService) {
        this.commentMapper = commentMapper;
        this.postMapper = postMapper;
        this.userService = userService;
    }

    /**
     * 创建评论
     */
    @Transactional
    public CommentDto createComment(CommentRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Long postId = request.getPostId();
        Long parentCommentId = request.getParentCommentId();

        // 如果 postId 为 0，意为该条是评论的评论
        if (postId != null && postId == 0) {
            if (parentCommentId == null) {
                throw new IllegalArgumentException("ParentCommentId is required when postId is 0 to indicate a reply.");
            }
            // 验证父评论存在，并从中获取 postId
            Comment parentComment = commentMapper.findById(parentCommentId);
            if (parentComment == null || parentComment.isDeleted()) {
                throw ResourceNotFoundException.of("Parent Comment", parentCommentId);
            }
            postId = parentComment.getPostId();
        } else {
            // 对于顶级评论或 postId 不为 0 的回复，正常验证父评论
            if (parentCommentId != null) {
                if (!commentMapper.existsAndNotDeleted(parentCommentId)) {
                    throw ResourceNotFoundException.of("Comment", parentCommentId);
                }
            }
        }

        // 统一验证最终的 postId
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw ResourceNotFoundException.of("Post", postId);
        }
        if (!"PUBLISHED".equals(post.getStatus())) {
            throw new RuntimeException("Cannot comment on unpublished post");
        }

        // 创建评论
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(currentUser.getId());
        comment.setPostId(postId); // 使用最终确定的 postId
        comment.setParentCommentId(parentCommentId);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setIsDeleted(false);

        commentMapper.insert(comment);

        // 更新帖子评论计数
        postMapper.updateCommentCount(postId, true);

        return convertToCommentDto(commentMapper.findById(comment.getId()));
    }

    /**
     * 更新评论
     */
    @Transactional
    public CommentDto updateComment(Long commentId, CommentUpdateRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw ResourceNotFoundException.of("Comment", commentId);
        }

        // 检查权限
        if (!comment.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only edit your own comments");
        }

        if (comment.isDeleted()) {
            throw new RuntimeException("Cannot edit deleted comment");
        }

        // 更新评论
        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.update(comment);

        return convertToCommentDto(commentMapper.findById(commentId));
    }

    /**
     * 删除评论（软删除）
     */
    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw ResourceNotFoundException.of("Comment", commentId);
        }

        // 检查权限
        if (!comment.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own comments");
        }

        if (comment.isDeleted()) {
            throw new RuntimeException("Comment already deleted");
        }

        // 软删除评论
        commentMapper.softDeleteById(commentId);

        // 更新帖子评论计数
        postMapper.updateCommentCount(comment.getPostId(), false);
    }

    /**
     * 获取评论详情
     */
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentMapper.findById(commentId);
        if (comment == null || comment.isDeleted()) {
            throw ResourceNotFoundException.of("Comment", commentId);
        }

        return convertToCommentDto(comment);
    }

    /**
     * 获取帖子的评论（分层结构）
     */
    public PaginationUtil.PageResponse<CommentDto> getPostComments(Long postId,
            PaginationUtil.PageRequest pageRequest) {
        // 验证帖子存在
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw ResourceNotFoundException.of("Post", postId);
        }

        // 获取顶级评论
        List<Comment> topLevelComments = commentMapper.findTopLevelCommentsByPostId(postId,
                pageRequest.getOffset(),
                pageRequest.getLimit());

        List<CommentDto> commentDtos = topLevelComments.stream()
                .map(comment -> {
                    CommentDto dto = convertToCommentDto(comment);

                    // 获取前几条回复
                    List<Comment> replies = commentMapper.findRepliesByParentCommentId(comment.getId(), 0, 3);
                    dto.setReplies(replies.stream().map(this::convertToCommentDto).collect(Collectors.toList()));
                    dto.setReplyCount(commentMapper.countRepliesByParentCommentId(comment.getId()));

                    return dto;
                })
                .collect(Collectors.toList());

        long totalElements = commentMapper.countTopLevelCommentsByPostId(postId);
        return PaginationUtil.createPageResponse(commentDtos, pageRequest, totalElements);
    }

    /**
     * 获取帖子的所有顶级评论（分页）
     */
    public PaginationUtil.PageResponse<CommentDto> getTopLevelCommentsByPostId(Long postId,
            PaginationUtil.PageRequest pageRequest) {
        // 1. 验证帖子是否存在
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw ResourceNotFoundException.of("Post", postId);
        }

        // 2. 分页查询顶级评论
        List<Comment> topLevelComments = commentMapper.findTopLevelCommentsByPostId(
                postId,
                pageRequest.getOffset(),
                pageRequest.getLimit());

        // 3. 将 Comment 转换为 CommentDto
        List<CommentDto> commentDtos = topLevelComments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());

        // 4. 获取顶级评论总数
        long totalElements = commentMapper.countTopLevelCommentsByPostId(postId);

        // 5. 创建并返回分页响应
        return PaginationUtil.createPageResponse(commentDtos, pageRequest, totalElements);
    }

    /**
     * 获取评论的回复
     */
    public PaginationUtil.PageResponse<CommentDto> getCommentReplies(Long commentId,
            PaginationUtil.PageRequest pageRequest) {
        // 验证评论存在
        if (!commentMapper.existsAndNotDeleted(commentId)) {
            throw ResourceNotFoundException.of("Comment", commentId);
        }

        List<Comment> replies = commentMapper.findRepliesByParentCommentId(commentId,
                pageRequest.getOffset(),
                pageRequest.getLimit());

        List<CommentDto> replyDtos = replies.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());

        long totalElements = commentMapper.countRepliesByParentCommentId(commentId);
        return PaginationUtil.createPageResponse(replyDtos, pageRequest, totalElements);
    }

    /**
     * 获取用户的评论历史
     */
    public PaginationUtil.PageResponse<CommentDto> getUserComments(PaginationUtil.PageRequest pageRequest) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        List<Comment> comments = commentMapper.findByUserId(currentUser.getId(),
                pageRequest.getOffset(),
                pageRequest.getLimit());

        List<CommentDto> commentDtos = comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());

        long totalElements = commentMapper.countByUserId(currentUser.getId());
        return PaginationUtil.createPageResponse(commentDtos, pageRequest, totalElements);
    }

    /**
     * 获取指定用户的评论（公开）
     */
    public PaginationUtil.PageResponse<CommentDto> getUserComments(Long userId,
            PaginationUtil.PageRequest pageRequest) {
        // 验证用户存在
        userService.getUserById(userId);

        List<Comment> comments = commentMapper.findByUserId(userId,
                pageRequest.getOffset(),
                pageRequest.getLimit());

        // 只返回未删除的评论
        List<CommentDto> commentDtos = comments.stream()
                .filter(comment -> !comment.isDeleted())
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());

        long totalElements = commentMapper.countByUserId(userId);
        return PaginationUtil.createPageResponse(commentDtos, pageRequest, totalElements);
    }

    /**
     * 获取对当前用户内容的评论（“评论我的”）
     */
    public PaginationUtil.PageResponse<CommentDto> getCommentsForMe(PaginationUtil.PageRequest pageRequest) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Long userId = currentUser.getId();

        List<Comment> comments = commentMapper.findCommentsForUser(
                userId,
                pageRequest.getOffset(),
                pageRequest.getLimit());

        List<CommentDto> commentDtos = comments.stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());

        long totalElements = commentMapper.countCommentsForUser(userId);
        return PaginationUtil.createPageResponse(commentDtos, pageRequest, totalElements);
    }

    /**
     * 转换为CommentDto
     */
    private CommentDto convertToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getDisplayContent());
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

        // 设置是否可编辑（只有作者可以编辑自己的评论）
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            dto.setCanEdit(comment.getUserId().equals(currentUser.getId()) && !comment.isDeleted());
        } catch (Exception e) {
            dto.setCanEdit(false);
        }

        return dto;
    }
}
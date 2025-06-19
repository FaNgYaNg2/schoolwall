package com.fy.schoolwall.comment.controller;

import com.fy.schoolwall.comment.dto.CommentDto;
import com.fy.schoolwall.comment.dto.CommentRequest;
import com.fy.schoolwall.comment.dto.CommentUpdateRequest;
import com.fy.schoolwall.comment.service.CommentService;
import com.fy.schoolwall.common.util.PaginationUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 创建评论
     * POST /api/comments
     */
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentRequest request) {
        CommentDto comment = commentService.createComment(request);
        return ResponseEntity.ok(comment);
    }

    /**
     * 更新评论
     * PUT /api/comments/{commentId}
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request) {
        CommentDto comment = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(comment);
    }

    /**
     * 删除评论
     * DELETE /api/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取评论详情
     * GET /api/comments/{commentId}
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long commentId) {
        CommentDto comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    /**
     * 获取评论的回复
     * GET /api/comments/{commentId}/replies
     */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<PaginationUtil.PageResponse<CommentDto>> getCommentReplies(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, null, "ASC");
        PaginationUtil.PageResponse<CommentDto> replies = commentService.getCommentReplies(commentId, pageRequest);
        return ResponseEntity.ok(replies);
    }

    /**
     * 获取帖子的所有顶级评论
     * GET /api/comments/post/{postId}/toplevel
     */
    @GetMapping("/post/{postId}/toplevel")
    public ResponseEntity<PaginationUtil.PageResponse<CommentDto>> getTopLevelComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        // 默认按时间倒序排列顶级评论
        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, "createdAt", "DESC");
        PaginationUtil.PageResponse<CommentDto> comments = commentService.getTopLevelCommentsByPostId(postId,
                pageRequest);
        return ResponseEntity.ok(comments);
    }

    /**
     * 获取我的评论历史
     * GET /api/comments/me
     */
    @GetMapping("/me")
    public ResponseEntity<PaginationUtil.PageResponse<CommentDto>> getMyComments(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, sort, direction);
        PaginationUtil.PageResponse<CommentDto> comments = commentService.getUserComments(pageRequest);
        return ResponseEntity.ok(comments);
    }

    /**
     * 获取指定用户的评论
     * GET /api/comments/users/{userId}
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<PaginationUtil.PageResponse<CommentDto>> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, null, "DESC");
        PaginationUtil.PageResponse<CommentDto> comments = commentService.getUserComments(userId, pageRequest);
        return ResponseEntity.ok(comments);
    }
}
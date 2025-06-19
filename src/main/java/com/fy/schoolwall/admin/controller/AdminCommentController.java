package com.fy.schoolwall.admin.controller;

import com.fy.schoolwall.admin.service.AdminCommentService;
import com.fy.schoolwall.comment.dto.CommentDto;
import com.fy.schoolwall.common.util.PaginationUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    public AdminCommentController(AdminCommentService adminCommentService) {
        this.adminCommentService = adminCommentService;
    }

    /**
     * 获取所有评论（分页）
     * GET /api/admin/comments?page=0&size=10&isDeleted=false
     */
    @GetMapping
    public ResponseEntity<PaginationUtil.PageResponse<CommentDto>> getAllComments(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) Boolean isDeleted) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, sort, direction);

        PaginationUtil.PageResponse<CommentDto> comments;
        if (isDeleted != null) {
            comments = adminCommentService.getCommentsByDeletedStatus(isDeleted, pageRequest);
        } else {
            comments = adminCommentService.getAllComments(pageRequest);
        }

        return ResponseEntity.ok(comments);
    }

    /**
     * 获取用户的评论
     * GET /api/admin/comments/users/{userId}
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<PaginationUtil.PageResponse<CommentDto>> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, null, "DESC");
        PaginationUtil.PageResponse<CommentDto> comments = adminCommentService.getUserComments(userId, pageRequest);
        return ResponseEntity.ok(comments);
    }

    /**
     * 获取帖子的评论
     * GET /api/admin/comments/posts/{postId}
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PaginationUtil.PageResponse<CommentDto>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, null, "DESC");
        PaginationUtil.PageResponse<CommentDto> comments = adminCommentService.getPostComments(postId, pageRequest);
        return ResponseEntity.ok(comments);
    }

    /**
     * 软删除评论
     * PUT /api/admin/comments/{commentId}/soft-delete
     */
    @PutMapping("/{commentId}/soft-delete")
    public ResponseEntity<Map<String, String>> softDeleteComment(@PathVariable Long commentId) {
        adminCommentService.softDeleteComment(commentId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment soft deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 物理删除评论
     * DELETE /api/admin/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        adminCommentService.deleteComment(commentId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment permanently deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 批量软删除评论
     * PUT /api/admin/comments/batch/soft-delete
     */
    @PutMapping("/batch/soft-delete")
    public ResponseEntity<Map<String, String>> batchSoftDeleteComments(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> commentIds = (List<Long>) request.get("commentIds");

        if (commentIds == null || commentIds.isEmpty()) {
            throw new RuntimeException("Comment IDs are required");
        }

        adminCommentService.batchSoftDeleteComments(commentIds);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Batch soft deleted " + commentIds.size() + " comments successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 批量物理删除评论
     * DELETE /api/admin/comments/batch
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, String>> batchDeleteComments(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> commentIds = (List<Long>) request.get("commentIds");

        if (commentIds == null || commentIds.isEmpty()) {
            throw new RuntimeException("Comment IDs are required");
        }

        adminCommentService.batchDeleteComments(commentIds);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Batch permanently deleted " + commentIds.size() + " comments successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 根据删除状态获取评论
     * GET /api/admin/comments/deleted/{isDeleted}
     */
    @GetMapping("/deleted/{isDeleted}")
    public ResponseEntity<PaginationUtil.PageResponse<CommentDto>> getCommentsByDeletedStatus(
            @PathVariable Boolean isDeleted,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, null, "DESC");
        PaginationUtil.PageResponse<CommentDto> comments = adminCommentService.getCommentsByDeletedStatus(isDeleted,
                pageRequest);
        return ResponseEntity.ok(comments);
    }
}
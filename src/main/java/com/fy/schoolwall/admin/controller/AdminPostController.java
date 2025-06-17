package com.fy.schoolwall.admin.controller;

import com.fy.schoolwall.admin.dto.AdminPostActionRequest;
import com.fy.schoolwall.admin.service.AdminPostService;
import com.fy.schoolwall.common.util.PaginationUtil;
import com.fy.schoolwall.post.dto.PostDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/admin/posts")
public class AdminPostController {

    private final AdminPostService adminPostService;

    public AdminPostController(AdminPostService adminPostService) {
        this.adminPostService = adminPostService;
    }

    /**
     * 获取所有帖子（分页）
     * GET /admin/posts?page=0&size=10&status=PUBLISHED
     */
    @GetMapping
    public ResponseEntity<PaginationUtil.PageResponse<PostDto>> getAllPosts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String status) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, sort, direction);

        PaginationUtil.PageResponse<PostDto> posts;
        if (status != null) {
            posts = adminPostService.getPostsByStatus(status, pageRequest);
        } else {
            posts = adminPostService.getAllPosts(pageRequest);
        }

        return ResponseEntity.ok(posts);
    }

    /**
     * 获取待审核帖子
     * GET /admin/posts/review
     */
    @GetMapping("/review")
    public ResponseEntity<PaginationUtil.PageResponse<PostDto>> getPostsForReview(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, null, "DESC");
        PaginationUtil.PageResponse<PostDto> posts = adminPostService.getPostsForReview(pageRequest);
        return ResponseEntity.ok(posts);
    }

    /**
     * 获取帖子详情
     * GET /admin/posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId) {
        PostDto post = adminPostService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    /**
     * 更新帖子状态
     * PUT /admin/posts/{postId}/status
     */
    @PutMapping("/{postId}/status")
    public ResponseEntity<Map<String, String>> updatePostStatus(@PathVariable Long postId,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");
        if (status == null || status.trim().isEmpty()) {
            throw new RuntimeException("Status is required");
        }

        adminPostService.updatePostStatus(postId, status);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Post status updated to " + status);
        return ResponseEntity.ok(response);
    }

    /**
     * 设置帖子置顶状态
     * PUT /admin/posts/{postId}/top
     */
    @PutMapping("/{postId}/top")
    public ResponseEntity<Map<String, String>> setTopStatus(@PathVariable Long postId,
            @RequestBody Map<String, Boolean> request) {

        Boolean isTop = request.get("isTop");
        if (isTop == null) {
            throw new RuntimeException("isTop status is required");
        }

        adminPostService.setTopStatus(postId, isTop);

        Map<String, String> response = new HashMap<>();
        String action = isTop ? "set as top" : "removed from top";
        response.put("message", "Post " + action + " successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 设置帖子推荐状态
     * PUT /admin/posts/{postId}/recommended
     */
    @PutMapping("/{postId}/recommended")
    public ResponseEntity<Map<String, String>> setRecommendedStatus(@PathVariable Long postId,
            @RequestBody Map<String, Boolean> request) {

        Boolean isRecommended = request.get("isRecommended");
        if (isRecommended == null) {
            throw new RuntimeException("isRecommended status is required");
        }

        adminPostService.setRecommendedStatus(postId, isRecommended);

        Map<String, String> response = new HashMap<>();
        String action = isRecommended ? "set as recommended" : "removed from recommended";
        response.put("message", "Post " + action + " successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 删除帖子
     * DELETE /admin/posts/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        adminPostService.deletePost(postId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 执行帖子操作
     * POST /admin/posts/{postId}/action
     */
    @PostMapping("/{postId}/action")
    public ResponseEntity<Map<String, String>> executePostAction(@PathVariable Long postId,
            @Valid @RequestBody AdminPostActionRequest request) {

        adminPostService.executePostAction(postId, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Action '" + request.getAction() + "' executed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 批量更新帖子状态
     * PUT /admin/posts/batch/status
     */
    @PutMapping("/batch/status")
    public ResponseEntity<Map<String, String>> batchUpdatePostStatus(
            @RequestBody Map<String, Object> request) {

        @SuppressWarnings("unchecked")
        List<Long> postIds = (List<Long>) request.get("postIds");
        String status = (String) request.get("status");

        if (postIds == null || postIds.isEmpty()) {
            throw new RuntimeException("Post IDs are required");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new RuntimeException("Status is required");
        }

        adminPostService.batchUpdatePostStatus(postIds, status);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Batch status update (" + status + ") for " + postIds.size() + " posts completed");
        return ResponseEntity.ok(response);
    }
}
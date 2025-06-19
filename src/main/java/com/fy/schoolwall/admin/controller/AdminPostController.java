package com.fy.schoolwall.admin.controller;

import com.fy.schoolwall.admin.dto.AdminPostActionRequest;
import com.fy.schoolwall.admin.dto.BatchPostStatusRequest;
import com.fy.schoolwall.admin.service.AdminPostService;
import com.fy.schoolwall.common.util.PaginationUtil;
import com.fy.schoolwall.post.dto.PostDto;
import com.fy.schoolwall.common.enums.PostCategory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/posts")
public class AdminPostController {

    private final AdminPostService adminPostService;

    public AdminPostController(AdminPostService adminPostService) {
        this.adminPostService = adminPostService;
    }

    /**
     * 获取所有帖子（分页）
     * GET /api/admin/posts?page=0&size=10&status=PUBLISHED&category=TECH_SHARING
     */
    @GetMapping
    public ResponseEntity<PaginationUtil.PageResponse<PostDto>> getAllPosts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, sort, direction);

        PaginationUtil.PageResponse<PostDto> posts;
        if (status != null && category != null) {
            // 同时按状态和分类筛选 - 这里需要在PostMapper中添加相应方法
            PostCategory categoryEnum = PostCategory.fromCode(category);
            if (categoryEnum == null) {
                throw new RuntimeException("Invalid category: " + category);
            }
            posts = adminPostService.getPostsByCategory(categoryEnum, pageRequest);
        } else if (status != null) {
            posts = adminPostService.getPostsByStatus(status, pageRequest);
        } else if (category != null) {
            PostCategory categoryEnum = PostCategory.fromCode(category);
            if (categoryEnum == null) {
                throw new RuntimeException("Invalid category: " + category);
            }
            posts = adminPostService.getPostsByCategory(categoryEnum, pageRequest);
        } else {
            posts = adminPostService.getAllPosts(pageRequest);
        }

        return ResponseEntity.ok(posts);
    }

    /**
     * 获取待审核帖子
     * GET /api/admin/posts/review
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
     * GET /api/admin/posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId) {
        PostDto post = adminPostService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    /**
     * 更新帖子状态
     * PUT /api/admin/posts/{postId}/status
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
        response.put("postId", postId.toString());
        response.put("newStatus", status);
        return ResponseEntity.ok(response);
    }

    /**
     * 设置帖子置顶状态
     * PUT /api/admin/posts/{postId}/top
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
        response.put("postId", postId.toString());
        response.put("isTop", isTop.toString());
        return ResponseEntity.ok(response);
    }

    /**
     * 设置帖子推荐状态
     * PUT /api/admin/posts/{postId}/recommended
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
        response.put("postId", postId.toString());
        response.put("isRecommended", isRecommended.toString());
        return ResponseEntity.ok(response);
    }

    /**
     * 删除帖子
     * DELETE /api/admin/posts/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        adminPostService.deletePost(postId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        response.put("postId", postId.toString());
        return ResponseEntity.ok(response);
    }

    /**
     * 执行帖子操作
     * POST /api/admin/posts/{postId}/action
     */
    @PostMapping("/{postId}/action")
    public ResponseEntity<Map<String, String>> executePostAction(@PathVariable Long postId,
            @Valid @RequestBody AdminPostActionRequest request) {

        adminPostService.executePostAction(postId, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Action '" + request.getAction() + "' executed successfully");
        response.put("postId", postId.toString());
        response.put("action", request.getAction());
        if (request.getReason() != null) {
            response.put("reason", request.getReason());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 批量更新帖子状态
     * PUT /api/admin/posts/batch/status
     */
    @PutMapping("/batch/status")
    public ResponseEntity<Map<String, String>> batchUpdatePostStatus(
            @Valid @RequestBody BatchPostStatusRequest request) {

        adminPostService.batchUpdatePostStatus(request.getPostIds(), request.getStatus());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Batch status update (" + request.getStatus() + ") for " + request.getPostIds().size()
                + " posts completed");
        response.put("status", request.getStatus());
        response.put("count", String.valueOf(request.getPostIds().size()));
        return ResponseEntity.ok(response);
    }

    /**
     * 获取各分类帖子统计
     * GET /api/admin/posts/category-stats
     */
    @GetMapping("/category-stats")
    public ResponseEntity<Map<String, Object>> getCategoryStats() {
        Map<String, Object> stats = new HashMap<>();

        for (PostCategory category : PostCategory.values()) {
            long count = adminPostService.getPostCountByCategory(category);
            Map<String, Object> categoryInfo = new HashMap<>();
            categoryInfo.put("code", category.getCode());
            categoryInfo.put("displayName", category.getDisplayName());
            categoryInfo.put("count", count);
            stats.put(category.getCode(), categoryInfo);
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * 获取所有帖子分类
     * GET /api/admin/posts/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, String>>> getPostCategories() {
        List<Map<String, String>> categories = Arrays.stream(PostCategory.values())
                .map(category -> {
                    Map<String, String> categoryMap = new HashMap<>();
                    categoryMap.put("code", category.getCode());
                    categoryMap.put("displayName", category.getDisplayName());
                    return categoryMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }

    /**
     * 获取所有可用的帖子状态
     * GET /api/admin/posts/statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<List<Map<String, String>>> getPostStatuses() {
        List<Map<String, String>> statuses = List.of(
                Map.of("code", "DRAFT", "displayName", "草稿"),
                Map.of("code", "PUBLISHED", "displayName", "已发布"),
                Map.of("code", "HIDDEN", "displayName", "已隐藏"),
                Map.of("code", "DELETED", "displayName", "已删除"));

        return ResponseEntity.ok(statuses);
    }
}
package com.fy.schoolwall.post.controller;

import com.fy.schoolwall.common.util.PaginationUtil;
import com.fy.schoolwall.post.dto.*;
import com.fy.schoolwall.post.service.PostService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 创建帖子
     * POST /posts
     */
    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostDto post = postService.createPost(request);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    /**
     * 更新帖子
     * PUT /posts/{postId}
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request) {
        PostDto post = postService.updatePost(postId, request);
        return ResponseEntity.ok(post);
    }

    /**
     * 删除帖子
     * DELETE /posts/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取帖子详情
     * GET /posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId) {
        PostDto post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    /**
     * 根据slug获取帖子详情（公开访问，会增加浏览量）
     * GET /posts/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<PostDto> getPostBySlug(@PathVariable String slug) {
        PostDto post = postService.getPostBySlug(slug);
        return ResponseEntity.ok(post);
    }

    /**
     * 获取当前用户的帖子列表
     * GET /posts/me
     */
    @GetMapping("/me")
    public ResponseEntity<List<PostDto>> getMyPosts() {
        List<PostDto> posts = postService.getMyPosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * 获取帖子动态列表（分页）
     * GET /posts/feed?page=0&size=10
     */
    @GetMapping("/feed")
    public ResponseEntity<PaginationUtil.PageResponse<PostFeedItemDto>> getPostFeed(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, sort, direction);
        PaginationUtil.PageResponse<PostFeedItemDto> feed = postService.getPostFeed(pageRequest);
        return ResponseEntity.ok(feed);
    }

    /**
     * 根据分类获取帖子
     * GET /posts/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<PaginationUtil.PageResponse<PostFeedItemDto>> getPostsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, null, "DESC");
        PaginationUtil.PageResponse<PostFeedItemDto> posts = postService.getPostsByCategory(category, pageRequest);
        return ResponseEntity.ok(posts);
    }

    /**
     * 搜索帖子
     * GET /posts/search?keyword=关键词
     */
    @GetMapping("/search")
    public ResponseEntity<PaginationUtil.PageResponse<PostFeedItemDto>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, null, "DESC");
        PaginationUtil.PageResponse<PostFeedItemDto> posts = postService.searchPosts(keyword, pageRequest);
        return ResponseEntity.ok(posts);
    }

    /**
     * 获取置顶帖子
     * GET /posts/top
     */
    @GetMapping("/top")
    public ResponseEntity<List<PostFeedItemDto>> getTopPosts(
            @RequestParam(defaultValue = "5") Integer limit) {
        List<PostFeedItemDto> posts = postService.getTopPosts(limit);
        return ResponseEntity.ok(posts);
    }

    /**
     * 获取推荐帖子
     * GET /posts/recommended
     */
    @GetMapping("/recommended")
    public ResponseEntity<List<PostFeedItemDto>> getRecommendedPosts(
            @RequestParam(defaultValue = "5") Integer limit) {
        List<PostFeedItemDto> posts = postService.getRecommendedPosts(limit);
        return ResponseEntity.ok(posts);
    }

    /**
     * 发布帖子
     * PUT /posts/{postId}/publish
     */
    @PutMapping("/{postId}/publish")
    public ResponseEntity<PostDto> publishPost(@PathVariable Long postId) {
        PostDto post = postService.publishPost(postId);
        return ResponseEntity.ok(post);
    }
}
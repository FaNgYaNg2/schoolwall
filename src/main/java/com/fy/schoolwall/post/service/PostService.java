package com.fy.schoolwall.post.service;

import com.fy.schoolwall.common.exception.ResourceNotFoundException;
import com.fy.schoolwall.common.util.PaginationUtil;
import com.fy.schoolwall.post.dto.*;
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
public class PostService {

    private final PostMapper postMapper;
    private final UserService userService;

    public PostService(PostMapper postMapper, UserService userService) {
        this.postMapper = postMapper;
        this.userService = userService;
    }

    /**
     * 创建帖子
     */
    @Transactional
    public PostDto createPost(CreatePostRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthorId(currentUser.getId());
        post.setAuthorUsername(currentUser.getUsername());
        post.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        post.setCategory(request.getCategory());
        post.setTags(request.getTags());
        post.setCoverImage(request.getCoverImage());
        post.setViewCount(0);
        post.setCommentCount(0);
        post.setIsTop(false);
        post.setIsRecommended(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        // 生成slug
        post.generateUniqueSlug();

        // 如果是发布状态，设置发布时间
        if ("PUBLISHED".equals(post.getStatus())) {
            post.setPublishedAt(LocalDateTime.now());
        }

        postMapper.insert(post);
        return convertToPostDto(post);
    }

    /**
     * 更新帖子
     */
    @Transactional
    public PostDto updatePost(Long postId, UpdatePostRequest request) {
        Post post = findPostById(postId); // 改名避免冲突
        User currentUser = userService.getCurrentAuthenticatedUser();

        // 检查权限：只有作者本人可以编辑
        if (!post.getAuthorId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only edit your own posts");
        }

        boolean needsNewSlug = false;

        // 更新字段
        if (request.getTitle() != null && !request.getTitle().equals(post.getTitle())) {
            post.setTitle(request.getTitle());
            needsNewSlug = true;
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            post.setCategory(request.getCategory());
        }
        if (request.getTags() != null) {
            post.setTags(request.getTags());
        }
        if (request.getCoverImage() != null) {
            post.setCoverImage(request.getCoverImage());
        }

        // 处理状态变更
        if (request.getStatus() != null && !request.getStatus().equals(post.getStatus())) {
            String oldStatus = post.getStatus();
            post.setStatus(request.getStatus());

            // 如果从草稿变为发布状态，设置发布时间
            if ("DRAFT".equals(oldStatus) && "PUBLISHED".equals(request.getStatus())) {
                post.setPublishedAt(LocalDateTime.now());
            }
        }

        // 如果标题有变化，重新生成slug
        if (needsNewSlug) {
            post.generateUniqueSlug();
        }

        post.setUpdatedAt(LocalDateTime.now());
        postMapper.update(post);

        return convertToPostDto(post);
    }

    /**
     * 删除帖子
     */
    @Transactional
    public void deletePost(Long postId) {
        Post post = findPostById(postId); // 使用私有方法
        User currentUser = userService.getCurrentAuthenticatedUser();

        // 检查权限：只有作者本人可以删除
        if (!post.getAuthorId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own posts");
        }

        postMapper.deleteById(postId);
    }

    /**
     * 根据ID获取帖子详情（公开方法，返回DTO）
     */
    public PostDto getPostById(Long postId) {
        Post post = findPostById(postId); // 使用私有方法获取Post对象
        return convertToPostDto(post);
    }

    /**
     * 根据slug获取帖子并增加浏览量
     */
    @Transactional
    public PostDto getPostBySlug(String slug) {
        Post post = postMapper.findBySlug(slug);
        if (post == null) {
            throw ResourceNotFoundException.of("Post", "slug", slug);
        }

        // 只有已发布的帖子才能被访问并增加浏览量
        if ("PUBLISHED".equals(post.getStatus())) {
            postMapper.updateViewCount(post.getId());
            post.setViewCount(post.getViewCount() + 1);
        }

        return convertToPostDto(post);
    }

    /**
     * 获取用户的帖子列表
     */
    public List<PostDto> getMyPosts() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<Post> posts = postMapper.findByAuthorId(currentUser.getId());
        return posts.stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取帖子动态列表（分页）
     */
    public PaginationUtil.PageResponse<PostFeedItemDto> getPostFeed(PaginationUtil.PageRequest pageRequest) {
        List<Post> posts = postMapper.findPublishedPosts(pageRequest.getOffset(), pageRequest.getLimit());
        List<PostFeedItemDto> feedItems = posts.stream()
                .map(this::convertToFeedItemDto)
                .collect(Collectors.toList());

        long totalElements = postMapper.countByStatus("PUBLISHED");
        return PaginationUtil.createPageResponse(feedItems, pageRequest, totalElements);
    }

    /**
     * 根据分类获取帖子
     */
    public PaginationUtil.PageResponse<PostFeedItemDto> getPostsByCategory(String category,
            PaginationUtil.PageRequest pageRequest) {
        List<Post> posts = postMapper.findPostsByCategory(category, pageRequest.getOffset(), pageRequest.getLimit());
        List<PostFeedItemDto> feedItems = posts.stream()
                .map(this::convertToFeedItemDto)
                .collect(Collectors.toList());

        long totalElements = postMapper.countByCategory(category);
        return PaginationUtil.createPageResponse(feedItems, pageRequest, totalElements);
    }

    /**
     * 搜索帖子
     */
    public PaginationUtil.PageResponse<PostFeedItemDto> searchPosts(String keyword,
            PaginationUtil.PageRequest pageRequest) {
        List<Post> posts = postMapper.searchPosts(keyword, pageRequest.getOffset(), pageRequest.getLimit());
        List<PostFeedItemDto> feedItems = posts.stream()
                .map(this::convertToFeedItemDto)
                .collect(Collectors.toList());

        // 这里简化处理，实际应该有专门的搜索计数方法
        long totalElements = posts.size();
        return PaginationUtil.createPageResponse(feedItems, pageRequest, totalElements);
    }

    /**
     * 获取置顶帖子
     */
    public List<PostFeedItemDto> getTopPosts(int limit) {
        List<Post> posts = postMapper.findTopPosts(limit);
        return posts.stream()
                .map(this::convertToFeedItemDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取推荐帖子
     */
    public List<PostFeedItemDto> getRecommendedPosts(int limit) {
        List<Post> posts = postMapper.findRecommendedPosts(limit);
        return posts.stream()
                .map(this::convertToFeedItemDto)
                .collect(Collectors.toList());
    }

    /**
     * 发布帖子
     */
    @Transactional
    public PostDto publishPost(Long postId) {
        Post post = findPostById(postId); // 使用私有方法
        User currentUser = userService.getCurrentAuthenticatedUser();

        // 检查权限：只有作者本人可以操作
        if (!post.getAuthorId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only publish your own posts");
        }

        if ("PUBLISHED".equals(post.getStatus())) {
            throw new RuntimeException("Post is already published");
        }

        post.setStatus("PUBLISHED");
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.update(post);

        return convertToPostDto(post);
    }

    // 私有辅助方法
    /**
     * 内部使用的方法，返回Post对象，避免方法名冲突
     */
    private Post findPostById(Long postId) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw ResourceNotFoundException.of("Post", postId);
        }
        return post;
    }

    /**
     * 将Post对象转换为PostDto
     */
    private PostDto convertToPostDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setSlug(post.getSlug());
        dto.setAuthorId(post.getAuthorId());
        dto.setAuthorUsername(post.getAuthorUsername());
        dto.setStatus(post.getStatus());
        dto.setCategory(post.getCategory());
        dto.setTags(post.getTags());
        dto.setCoverImage(post.getCoverImage());
        dto.setViewCount(post.getViewCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setIsTop(post.getIsTop());
        dto.setIsRecommended(post.getIsRecommended());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setPublishedAt(post.getPublishedAt());
        return dto;
    }

    /**
     * 将Post对象转换为PostFeedItemDto
     */
    private PostFeedItemDto convertToFeedItemDto(Post post) {
        PostFeedItemDto dto = new PostFeedItemDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setSlug(post.getSlug());
        dto.setAuthorUsername(post.getAuthorUsername());
        dto.setCategory(post.getCategory());
        dto.setCoverImage(post.getCoverImage());
        dto.setViewCount(post.getViewCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setIsTop(post.getIsTop());
        dto.setIsRecommended(post.getIsRecommended());
        dto.setPublishedAt(post.getPublishedAt());
        dto.generateSummary(post.getContent());
        return dto;
    }
}
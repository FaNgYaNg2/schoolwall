package com.fy.schoolwall.admin.service;

import com.fy.schoolwall.admin.dto.AdminPostActionRequest;
import com.fy.schoolwall.common.enums.UserRole;
import com.fy.schoolwall.common.exception.ResourceNotFoundException;
import com.fy.schoolwall.common.util.PaginationUtil;
import com.fy.schoolwall.post.dto.PostDto;
import com.fy.schoolwall.post.model.Post;
import com.fy.schoolwall.post.repository.PostMapper;
import com.fy.schoolwall.comment.model.Comment;
import com.fy.schoolwall.comment.repository.CommentMapper;
import com.fy.schoolwall.user.model.User;
import com.fy.schoolwall.user.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminPostService {

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;

    public AdminPostService(PostMapper postMapper, CommentMapper commentMapper, UserService userService) {
        this.postMapper = postMapper;
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
     * 获取所有帖子（分页）
     */
    public PaginationUtil.PageResponse<PostDto> getAllPosts(PaginationUtil.PageRequest pageRequest) {
        validateAdminAccess();

        List<Post> posts = postMapper.findAllPosts(pageRequest.getOffset(), pageRequest.getLimit());
        List<PostDto> postDtos = posts.stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList());

        // 这里简化处理，实际应该有专门的总数统计
        long totalElements = posts.size();
        return PaginationUtil.createPageResponse(postDtos, pageRequest, totalElements);
    }

    /**
     * 根据状态获取帖子
     */
    public PaginationUtil.PageResponse<PostDto> getPostsByStatus(String status,
            PaginationUtil.PageRequest pageRequest) {
        validateAdminAccess();

        List<Post> posts = postMapper.findPostsByStatus(status, pageRequest.getOffset(), pageRequest.getLimit());
        List<PostDto> postDtos = posts.stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList());

        long totalElements = postMapper.countByStatus(status);
        return PaginationUtil.createPageResponse(postDtos, pageRequest, totalElements);
    }

    /**
     * 获取待审核帖子
     */
    public PaginationUtil.PageResponse<PostDto> getPostsForReview(PaginationUtil.PageRequest pageRequest) {
        validateAdminAccess();

        List<Post> posts = postMapper.findPostsForReview(pageRequest.getOffset(), pageRequest.getLimit());
        List<PostDto> postDtos = posts.stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList());

        // 简化处理
        long totalElements = posts.size();
        return PaginationUtil.createPageResponse(postDtos, pageRequest, totalElements);
    }

    /**
     * 管理员获取帖子详情
     */
    public PostDto getPostById(Long postId) {
        validateAdminAccess();

        Post post = postMapper.findById(postId);
        if (post == null) {
            throw ResourceNotFoundException.of("Post", postId);
        }

        return convertToPostDto(post);
    }

    /**
     * 管理员更新帖子状态
     */
    @Transactional
    public void updatePostStatus(Long postId, String status) {
        validateAdminAccess();

        Post post = postMapper.findById(postId);
        if (post == null) {
            throw ResourceNotFoundException.of("Post", postId);
        }

        // 如果状态改为发布，设置发布时间
        if ("PUBLISHED".equals(status) && !"PUBLISHED".equals(post.getStatus())) {
            post.setPublishedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());
            postMapper.update(post);
        }

        postMapper.updateStatus(postId, status);

        User currentUser = userService.getCurrentAuthenticatedUser();
        System.out.println("Post status updated by admin. Post ID: " + postId +
                ", New Status: " + status + ", Admin ID: " + currentUser.getId());
    }

    /**
     * 设置帖子置顶状态
     */
    @Transactional
    public void setTopStatus(Long postId, boolean isTop) {
        validateAdminAccess();

        Post post = postMapper.findById(postId);
        if (post == null) {
            throw ResourceNotFoundException.of("Post", postId);
        }

        postMapper.setTopStatus(postId, isTop);

        User currentUser = userService.getCurrentAuthenticatedUser();
        String action = isTop ? "set as top" : "removed from top";
        System.out.println("Post " + action + " by admin. Post ID: " + postId +
                ", Admin ID: " + currentUser.getId());
    }

    /**
     * 设置帖子推荐状态
     */
    @Transactional
    public void setRecommendedStatus(Long postId, boolean isRecommended) {
        validateAdminAccess();

        Post post = postMapper.findById(postId);
        if (post == null) {
            throw ResourceNotFoundException.of("Post", postId);
        }

        postMapper.setRecommendedStatus(postId, isRecommended);

        User currentUser = userService.getCurrentAuthenticatedUser();
        String action = isRecommended ? "set as recommended" : "removed from recommended";
        System.out.println("Post " + action + " by admin. Post ID: " + postId +
                ", Admin ID: " + currentUser.getId());
    }

    /**
     * 管理员删除帖子
     */
    @Transactional
    public void deletePost(Long postId) {
        validateAdminAccess();

        Post post = postMapper.findById(postId);
        if (post == null) {
            throw ResourceNotFoundException.of("Post", postId);
        }

        // 先删除相关评论
        commentMapper.batchSoftDelete(
                commentMapper.findByPostId(postId, 0, Integer.MAX_VALUE)
                        .stream().map(Comment::getId).collect(Collectors.toList()));

        // 再删除帖子
        postMapper.deleteById(postId);

        System.out.println("Post and related comments deleted by admin. Post ID: " + postId);
    }

    /**
     * 批量操作帖子
     */
    @Transactional
    public void batchUpdatePostStatus(List<Long> postIds, String status) {
        validateAdminAccess();

        User currentUser = userService.getCurrentAuthenticatedUser();

        for (Long postId : postIds) {
            try {
                postMapper.updateStatus(postId, status);

                // 如果状态改为发布，设置发布时间
                if ("PUBLISHED".equals(status)) {
                    Post post = postMapper.findById(postId);
                    if (post != null && !"PUBLISHED".equals(post.getStatus())) {
                        post.setPublishedAt(LocalDateTime.now());
                        post.setUpdatedAt(LocalDateTime.now());
                        postMapper.update(post);
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to update status for post ID: " + postId + ", Error: " + e.getMessage());
                // 继续处理其他帖子
            }
        }

        System.out.println("Batch status update (" + status + ") for " + postIds.size() +
                " posts by admin ID: " + currentUser.getId());
    }

    /**
     * 执行管理员操作
     */
    @Transactional
    public void executePostAction(Long postId, AdminPostActionRequest request) {
        switch (request.getAction().toLowerCase()) {
            case "approve":
                updatePostStatus(postId, "PUBLISHED");
                break;
            case "reject":
                updatePostStatus(postId, "HIDDEN");
                break;
            case "set_top":
                setTopStatus(postId, true);
                break;
            case "remove_top":
                setTopStatus(postId, false);
                break;
            case "set_recommended":
                setRecommendedStatus(postId, true);
                break;
            case "remove_recommended":
                setRecommendedStatus(postId, false);
                break;
            case "delete":
                deletePost(postId);
                break;
            default:
                throw new RuntimeException("Unknown action: " + request.getAction());
        }
    }

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
}
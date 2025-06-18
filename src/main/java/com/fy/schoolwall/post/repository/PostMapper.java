package com.fy.schoolwall.post.repository;

import com.fy.schoolwall.post.model.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

        // 基础CRUD操作
        Post findById(Long id);

        Post findBySlug(String slug);

        List<Post> findByAuthorId(Long authorId);

        void insert(Post post);

        void update(Post post);

        void deleteById(Long id);

        // 状态管理
        void updateStatus(@Param("id") Long id, @Param("status") String status);

        void updateViewCount(@Param("id") Long id);

        void updateCommentCount(@Param("id") Long id, @Param("increment") boolean increment);

        // 查询操作
        List<Post> findPublishedPosts(@Param("offset") int offset, @Param("limit") int limit);

        List<Post> findPostsByCategory(@Param("category") String category, @Param("offset") int offset,
                        @Param("limit") int limit);

        List<Post> findPostsByStatus(@Param("status") String status, @Param("offset") int offset,
                        @Param("limit") int limit);

        List<Post> findTopPosts(@Param("limit") int limit);

        List<Post> findRecommendedPosts(@Param("limit") int limit);

        List<Post> searchPosts(@Param("keyword") String keyword, @Param("offset") int offset,
                        @Param("limit") int limit);

        // 统计操作
        long countByStatus(@Param("status") String status);

        long countByAuthorId(@Param("authorId") Long authorId);

        long countByCategory(@Param("category") String category);

        // 获取所有分类及其帖子数量统计
        List<Map<String, Object>> getCategoryStats();

        // 管理员专用
        List<Post> findAllPosts(@Param("offset") int offset, @Param("limit") int limit);

        List<Post> findPostsForReview(@Param("offset") int offset, @Param("limit") int limit);

        void setTopStatus(@Param("id") Long id, @Param("isTop") boolean isTop);

        void setRecommendedStatus(@Param("id") Long id, @Param("isRecommended") boolean isRecommended);

        // 添加的方法
        long countAllPosts();

        long countPostsForReview();

        // 按状态和分类同时筛选的方法
        List<Post> findPostsByStatusAndCategory(@Param("status") String status,
                        @Param("category") String category,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

        long countByStatusAndCategory(@Param("status") String status, @Param("category") String category);
}
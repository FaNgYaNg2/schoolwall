package com.fy.schoolwall.comment.repository;

import com.fy.schoolwall.comment.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 基础CRUD操作
    Comment findById(Long id);

    void insert(Comment comment);

    void update(Comment comment);

    void deleteById(Long id);

    // 软删除
    void softDeleteById(Long id);

    // 根据帖子ID获取评论
    List<Comment> findByPostId(@Param("postId") Long postId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    // 根据帖子ID统计评论数
    long countByPostId(Long postId);

    // 根据用户ID获取评论
    List<Comment> findByUserId(@Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    // 根据用户ID统计评论数
    long countByUserId(Long userId);

    // 根据父评论ID获取回复
    List<Comment> findRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    // 根据父评论ID统计回复数
    long countRepliesByParentCommentId(Long parentCommentId);

    // 获取帖子的顶级评论
    List<Comment> findTopLevelCommentsByPostId(@Param("postId") Long postId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    // 统计帖子的顶级评论数
    long countTopLevelCommentsByPostId(Long postId);

    // 管理员查询
    List<Comment> findAllComments(@Param("offset") int offset, @Param("limit") int limit);

    long countAllComments();

    // 根据是否删除状态查询
    List<Comment> findCommentsByDeletedStatus(@Param("isDeleted") Boolean isDeleted,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countCommentsByDeletedStatus(Boolean isDeleted);

    // 检查评论是否属于指定用户
    boolean isCommentOwnedByUser(@Param("commentId") Long commentId, @Param("userId") Long userId);

    // 检查评论是否存在且未删除
    boolean existsAndNotDeleted(Long commentId);

    // 获取对用户内容的评论（帖子被评论、评论被回复）
    List<Comment> findCommentsForUser(@Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long countCommentsForUser(@Param("userId") Long userId);

    // 批量软删除
    void batchSoftDelete(@Param("commentIds") List<Long> commentIds);

    // 批量物理删除
    void batchDelete(@Param("commentIds") List<Long> commentIds);
}
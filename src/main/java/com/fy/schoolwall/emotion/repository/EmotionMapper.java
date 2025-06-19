package com.fy.schoolwall.emotion.repository;

import com.fy.schoolwall.emotion.model.Emotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EmotionMapper {
    Emotion findByPostId(@Param("postId") Long postId);
    Emotion findByCommentId(@Param("commentId") Long commentId);
    void insert(Emotion emotion);
    List<Emotion> findEmotionsForUserPosts(@Param("userId") Long userId);
    List<Emotion> findEmotionsForUserComments(@Param("userId") Long userId);
}

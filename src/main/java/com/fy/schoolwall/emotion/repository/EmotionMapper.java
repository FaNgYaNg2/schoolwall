package com.fy.schoolwall.emotion.repository;

import com.fy.schoolwall.emotion.model.Emotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmotionMapper {
    Emotion findByPostId(@Param("postId") Long postId);
    Emotion findByCommentId(@Param("commentId") Long commentId);
    void insert(Emotion emotion);
}

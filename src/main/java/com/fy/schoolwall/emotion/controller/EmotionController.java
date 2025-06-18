package com.fy.schoolwall.emotion.controller;

import com.fy.schoolwall.emotion.dto.EmotionDto;
import com.fy.schoolwall.emotion.service.EmotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emotion")
public class EmotionController {
    private final EmotionService emotionService;

    public EmotionController(EmotionService emotionService) {
        this.emotionService = emotionService;
    }

    /**
     * 获取或生成帖子的情绪分析
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<EmotionDto> getPostEmotion(@PathVariable Long postId) {
        return ResponseEntity.ok(emotionService.getOrAnalyzeByPostId(postId));
    }

    /**
     * 获取或生成评论的情绪分析
     */
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<EmotionDto> getCommentEmotion(@PathVariable Long commentId) {
        return ResponseEntity.ok(emotionService.getOrAnalyzeByCommentId(commentId));
    }
}
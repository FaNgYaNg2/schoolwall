package com.fy.schoolwall.emotion.service;

import com.fy.schoolwall.emotion.model.Emotion;
import com.fy.schoolwall.emotion.repository.EmotionMapper;
import com.fy.schoolwall.emotion.dto.EmotionDto;
import com.fy.schoolwall.post.repository.PostMapper;
import com.fy.schoolwall.comment.repository.CommentMapper;
import com.fy.schoolwall.post.model.Post;
import com.fy.schoolwall.comment.model.Comment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EmotionService {
    private final EmotionMapper emotionMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmotionService(EmotionMapper emotionMapper, PostMapper postMapper, CommentMapper commentMapper) {
        this.emotionMapper = emotionMapper;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.restTemplate = new RestTemplate();
    }

    public EmotionDto getOrAnalyzeByPostId(Long postId) {
        Emotion emotion = emotionMapper.findByPostId(postId);
        if (emotion != null) {
            return toDto(emotion);
        }
        Post post = postMapper.findById(postId);
        if (post == null)
            throw new RuntimeException("Post not found");
        return analyzeAndSave(post.getContent(), postId, null);
    }

    public EmotionDto getOrAnalyzeByCommentId(Long commentId) {
        Emotion emotion = emotionMapper.findByCommentId(commentId);
        if (emotion != null) {
            return toDto(emotion);
        }
        Comment comment = commentMapper.findById(commentId);
        if (comment == null)
            throw new RuntimeException("Comment not found");
        return analyzeAndSave(comment.getContent(), null, commentId);
    }

    private EmotionDto analyzeAndSave(String text, Long postId, Long commentId) {
        // 调用外部API
        String url = "http://localhost:5000/analyze_sentiment";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> req = Map.of("text", text);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(req, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        try {
            // 解析API返回
            Map<String, Object> result = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<Map<String, Object>>() {
                    });
            if (!(Boolean) result.getOrDefault("success", false)) {
                throw new RuntimeException("Emotion API error: " + result.get("error"));
            }
            Emotion emotion = new Emotion();
            emotion.setPostId(postId);
            emotion.setCommentId(commentId);
            emotion.setText(text);
            emotion.setSentiment((String) result.get("sentiment"));
            emotion.setConfidence(
                    result.get("confidence") != null ? Double.valueOf(result.get("confidence").toString()) : null);
            emotion.setProbabilitiesJson(objectMapper.writeValueAsString(result.get("probabilities")));
            emotion.setCreatedAt(LocalDateTime.now());
            emotionMapper.insert(emotion);
            return toDto(emotion);
        } catch (Exception e) {
            throw new RuntimeException("Emotion analysis failed", e);
        }
    }

    private EmotionDto toDto(Emotion emotion) {
        EmotionDto dto = new EmotionDto();
        dto.setId(emotion.getId());
        dto.setPostId(emotion.getPostId());
        dto.setCommentId(emotion.getCommentId());
        dto.setText(emotion.getText());
        dto.setSentiment(emotion.getSentiment());
        dto.setConfidence(emotion.getConfidence());
        dto.setCreatedAt(emotion.getCreatedAt());
        try {
            dto.setProbabilities(
                    objectMapper.readValue(emotion.getProbabilitiesJson(), new TypeReference<Map<String, Double>>() {
                    }));
        } catch (JsonProcessingException e) {
            dto.setProbabilities(null);
        }
        return dto;
    }
}
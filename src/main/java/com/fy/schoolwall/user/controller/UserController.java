package com.fy.schoolwall.user.controller;

import com.fy.schoolwall.user.dto.*;
import com.fy.schoolwall.user.model.User;
import com.fy.schoolwall.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取指定用户所有内容的综合情绪分析统计。
     * GET /api/users/{userId}/emotions
     */
    @GetMapping("/{userId}/emotions")
    public ResponseEntity<UserEmotionStatsDto> getUserEmotionStats(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserEmotionStats(userId));
    }

    /**
     * 获取当前认证用户的个人资料。
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile() {
        User user = userService.getCurrentAuthenticatedUser();

        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setRole(user.getRole());

        return ResponseEntity.ok(dto);
    }

    /**
     * 更新当前认证用户的个人资料。
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateMyProfile(@Valid @RequestBody UserUpdateRequest updateRequest) {
        User updatedUser = userService.updateUserProfile(updateRequest);

        UserProfileDto dto = new UserProfileDto();
        dto.setId(updatedUser.getId());
        dto.setUsername(updatedUser.getUsername());
        dto.setEmail(updatedUser.getEmail());
        dto.setAvatarUrl(updatedUser.getAvatarUrl());
        dto.setBio(updatedUser.getBio());
        dto.setRole(updatedUser.getRole());

        return ResponseEntity.ok(dto);
    }

    /**
     * 修改当前认证用户的密码。
     * PUT /api/users/me/password
     */
    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {

        userService.changePassword(passwordChangeRequest);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 删除当前用户账户（软删除）
     * DELETE /api/users/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteMyAccount(@Valid @RequestBody DeleteAccountRequest deleteRequest) {
        userService.deleteCurrentUserAccount(deleteRequest.getPassword());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Account deleted successfully");
        return ResponseEntity.ok(response);
    }
}
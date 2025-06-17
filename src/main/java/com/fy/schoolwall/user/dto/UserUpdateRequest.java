package com.fy.schoolwall.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    // 允许用户修改的字段，例如邮箱、头像URL、简介
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 255, message = "Avatar URL cannot exceed 255 characters")
    private String avatarUrl;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    // 不允许直接通过此接口修改用户名、密码、ID、角色等敏感信息
    // 密码修改应通过单独的“修改密码”接口进行
}
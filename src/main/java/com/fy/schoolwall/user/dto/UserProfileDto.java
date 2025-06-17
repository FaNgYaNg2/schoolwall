package com.fy.schoolwall.user.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private String bio;
    private String role; // 也可以选择不暴露角色信息给普通用户
}
package com.fy.schoolwall.user.model;

import com.fy.schoolwall.common.util.SlugGenerator;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash; // 数据库中存储的密码哈希
    private String role;
    private String avatarUrl;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isEnabled;
    private Boolean isLocked;

    /**
     * 生成用户名slug（用于URL友好的用户标识）
     */
    public String getUsernameSlug() {
        return SlugGenerator.generateSlug(this.username);
    }

    /**
     * 验证用户名slug格式
     */
    public boolean isValidUsernameSlug(String slug) {
        return SlugGenerator.isValidSlug(slug);
    }
}
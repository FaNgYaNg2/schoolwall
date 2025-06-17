package com.fy.schoolwall.admin.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String avatarUrl;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isEnabled;
    private Boolean isLocked;
}
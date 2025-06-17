package com.fy.schoolwall.common.enums;

/**
 * 用户角色枚举
 * 
 * 意义：
 * 1. 统一管理系统中的用户角色类型
 * 2. 避免硬编码字符串，提高代码可维护性
 * 3. 提供类型安全的角色检查
 * 4. 便于权限控制和角色验证
 */
public enum UserRole {

    /**
     * 普通用户 - 基础权限
     * 可以：浏览内容、发表评论、管理个人资料
     */
    USER("USER", "普通用户"),

    /**
     * 内容审核员 - 中级权限
     * 可以：审核用户发布的内容、管理评论
     */
    MODERATOR("MODERATOR", "审核员"),

    /**
     * 系统管理员 - 最高权限
     * 可以：管理用户、系统配置、所有数据操作
     */
    ADMIN("ADMIN", "管理员"),

    /**
     * 游客 - 临时角色
     * 可以：仅浏览公开内容
     */
    GUEST("GUEST", "游客");

    private final String code;
    private final String displayName;

    UserRole(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据代码获取角色枚举
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role code: " + code);
    }

    /**
     * 检查是否为管理员角色
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * 检查是否为审核员或更高权限
     */
    public boolean isModerator() {
        return this == MODERATOR || this == ADMIN;
    }

    /**
     * 检查是否为普通用户或更高权限
     */
    public boolean isUser() {
        return this == USER || this == MODERATOR || this == ADMIN;
    }
}
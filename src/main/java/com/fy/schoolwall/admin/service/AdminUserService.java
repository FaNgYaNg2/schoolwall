package com.fy.schoolwall.admin.service;

import com.fy.schoolwall.admin.dto.AdminUserDto;
import com.fy.schoolwall.common.enums.UserRole;
import com.fy.schoolwall.common.util.PaginationUtil;
import com.fy.schoolwall.user.model.User;
import com.fy.schoolwall.user.repository.UserMapper;
import com.fy.schoolwall.user.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final UserMapper userMapper;
    private final UserService userService;

    public AdminUserService(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    /**
     * 验证当前用户是否为管理员
     */
    private void validateAdminAccess() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        userService.validateUserRole(currentUser, UserRole.ADMIN);
    }

    /**
     * 获取所有用户列表（分页）
     */
    public PaginationUtil.PageResponse<AdminUserDto> getAllUsers(PaginationUtil.PageRequest pageRequest) {
        validateAdminAccess();

        // 这里需要在UserMapper中添加分页查询方法
        List<User> users = userMapper.findAllUsers(); // 临时使用现有方法

        // 手动分页（生产环境应该在数据库层面分页）
        int totalElements = users.size();
        int startIndex = pageRequest.getOffset();
        int endIndex = Math.min(startIndex + pageRequest.getSize(), totalElements);

        List<User> pagedUsers = users.subList(startIndex, endIndex);
        List<AdminUserDto> userDtos = pagedUsers.stream()
                .map(this::convertToAdminUserDto)
                .collect(Collectors.toList());

        return PaginationUtil.createPageResponse(userDtos, pageRequest, totalElements);
    }

    /**
     * 获取所有用户列表（无分页，保持向后兼容）
     */
    public List<AdminUserDto> getAllUsers() {
        validateAdminAccess();

        List<User> users = userMapper.findAllUsers();
        return users.stream()
                .map(this::convertToAdminUserDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据状态获取用户列表
     */
    public List<AdminUserDto> getUsersByStatus(boolean enabled) {
        validateAdminAccess();

        List<User> users = userMapper.findUsersByStatus(enabled);
        return users.stream()
                .map(this::convertToAdminUserDto)
                .collect(Collectors.toList());
    }

    /**
     * 管理员删除用户账户（硬删除）
     */
    @Transactional
    public void deleteUserByAdmin(Long userId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        validateAdminAccess();

        // 不允许管理员删除自己
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("Cannot delete your own account.");
        }

        try {
            userMapper.deleteById(userId);
            System.out.println(
                    "User account hard deleted by admin. User ID: " + userId + ", Admin ID: " + currentUser.getId());
        } catch (Exception e) {
            System.err.println("Database error while admin deleting user: " + e.getMessage());
            throw new RuntimeException("Failed to delete user account: " + e.getMessage(), e);
        }
    }

    /**
     * 管理员切换用户状态（启用/禁用）
     */
    @Transactional
    public void toggleUserStatus(Long userId, boolean enabled) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        validateAdminAccess();

        // 不允许管理员禁用自己
        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("Cannot modify your own account status.");
        }

        try {
            userMapper.updateUserStatus(userId, enabled);
            String action = enabled ? "enabled" : "disabled";
            System.out.println(
                    "User account " + action + " by admin. User ID: " + userId + ", Admin ID: " + currentUser.getId());
        } catch (Exception e) {
            System.err.println("Database error while updating user status: " + e.getMessage());
            throw new RuntimeException("Failed to update user status: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID获取用户详情
     */
    public AdminUserDto getUserById(Long userId) {
        validateAdminAccess();
        User user = userService.getUserById(userId);
        return convertToAdminUserDto(user);
    }

    /**
     * 批量操作用户状态
     */
    @Transactional
    public void batchToggleUserStatus(List<Long> userIds, boolean enabled) {
        validateAdminAccess();

        User currentUser = userService.getCurrentAuthenticatedUser();

        for (Long userId : userIds) {
            // 跳过管理员自己
            if (currentUser.getId().equals(userId)) {
                continue;
            }

            try {
                userMapper.updateUserStatus(userId, enabled);
            } catch (Exception e) {
                System.err.println("Failed to update status for user ID: " + userId + ", Error: " + e.getMessage());
                // 继续处理其他用户，不抛出异常
            }
        }

        String action = enabled ? "enabled" : "disabled";
        System.out.println("Batch " + action + " " + userIds.size() + " users by admin ID: " + currentUser.getId());
    }

    /**
     * 根据角色获取用户列表
     */
    public List<AdminUserDto> getUsersByRole(UserRole role) {
        validateAdminAccess();

        List<User> allUsers = userMapper.findAllUsers();
        return allUsers.stream()
                .filter(user -> role.getCode().equals(user.getRole()))
                .map(this::convertToAdminUserDto)
                .collect(Collectors.toList());
    }

    /**
     * 将User对象转换为AdminUserDto
     */
    private AdminUserDto convertToAdminUserDto(User user) {
        AdminUserDto dto = new AdminUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setIsEnabled(user.getIsEnabled());
        dto.setIsLocked(user.getIsLocked());
        return dto;
    }
}
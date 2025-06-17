package com.fy.schoolwall.user.service;

import com.fy.schoolwall.common.enums.UserRole;
import com.fy.schoolwall.common.exception.ResourceNotFoundException;
import com.fy.schoolwall.user.dto.PasswordChangeRequest;
import com.fy.schoolwall.user.dto.UserUpdateRequest;
import com.fy.schoolwall.user.model.User;
import com.fy.schoolwall.user.repository.UserMapper;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 获取当前认证用户的完整信息。
     * 
     * @return 当前认证的用户对象，如果未认证则返回 null。
     */
    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() instanceof String
                        && "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userMapper.findByUsername(userDetails.getUsername());

        // 使用ResourceNotFoundException替代返回null
        if (user == null) {
            throw ResourceNotFoundException.of("User", "username", userDetails.getUsername());
        }

        return user;
    }

    /**
     * 根据ID获取用户，如果不存在抛出异常
     */
    public User getUserById(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw ResourceNotFoundException.of("User", userId);
        }
        return user;
    }

    /**
     * 验证用户权限
     */
    public void validateUserRole(User user, UserRole requiredRole) {
        UserRole userRole = UserRole.fromCode(user.getRole());

        switch (requiredRole) {
            case ADMIN:
                if (!userRole.isAdmin()) {
                    throw new RuntimeException("Access denied. Admin privileges required.");
                }
                break;
            case MODERATOR:
                if (!userRole.isModerator()) {
                    throw new RuntimeException("Access denied. Moderator privileges required.");
                }
                break;
            case USER:
                if (!userRole.isUser()) {
                    throw new RuntimeException("Access denied. User privileges required.");
                }
                break;
            default:
                throw new RuntimeException("Invalid role requirement.");
        }
    }

    /**
     * 更新当前认证用户的个人资料。
     */
    @Transactional
    public User updateUserProfile(@Valid UserUpdateRequest updateRequest) {
        User currentUser = getCurrentAuthenticatedUser();

        // 改进邮箱检查逻辑
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty()
                && !updateRequest.getEmail().equals(currentUser.getEmail())) {
            User existingUserWithEmail = userMapper.findByEmail(updateRequest.getEmail());
            if (existingUserWithEmail != null && !existingUserWithEmail.getId().equals(currentUser.getId())) {
                throw new RuntimeException(
                        "Email '" + updateRequest.getEmail() + "' is already in use by another account.");
            }
        }

        // 改进字段更新逻辑
        boolean changed = false;

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty()) {
            if (!updateRequest.getEmail().equals(currentUser.getEmail())) {
                currentUser.setEmail(updateRequest.getEmail().trim());
                changed = true;
            }
        }

        if (updateRequest.getAvatarUrl() != null) {
            String newAvatarUrl = updateRequest.getAvatarUrl().trim();
            if (!newAvatarUrl.equals(currentUser.getAvatarUrl() != null ? currentUser.getAvatarUrl() : "")) {
                currentUser.setAvatarUrl(newAvatarUrl.isEmpty() ? null : newAvatarUrl);
                changed = true;
            }
        }

        if (updateRequest.getBio() != null) {
            String newBio = updateRequest.getBio().trim();
            if (!newBio.equals(currentUser.getBio() != null ? currentUser.getBio() : "")) {
                currentUser.setBio(newBio.isEmpty() ? null : newBio);
                changed = true;
            }
        }

        if (changed) {
            currentUser.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            try {
                userMapper.update(currentUser);
                System.out.println("User ID: " + currentUser.getId() + " updated successfully.");
            } catch (Exception e) {
                System.err.println("Database update error: " + e.getMessage());
                throw new RuntimeException("Failed to update user profile: " + e.getMessage(), e);
            }
        } else {
            System.out
                    .println("No changes detected for user ID: " + currentUser.getId() + ". Skipping database update.");
        }

        return currentUser;
    }

    /**
     * 修改当前认证用户的密码。
     */
    @Transactional
    public void changePassword(@Valid PasswordChangeRequest passwordChangeRequest) {
        User currentUser = getCurrentAuthenticatedUser();

        // 验证新密码和确认密码是否匹配
        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation password do not match.");
        }

        // 验证当前密码是否正确
        if (!passwordEncoder.matches(passwordChangeRequest.getCurrentPassword(), currentUser.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect.");
        }

        // 检查新密码是否与当前密码相同
        if (passwordEncoder.matches(passwordChangeRequest.getNewPassword(), currentUser.getPasswordHash())) {
            throw new RuntimeException("New password must be different from current password.");
        }

        try {
            String newPasswordHash = passwordEncoder.encode(passwordChangeRequest.getNewPassword());
            userMapper.updatePassword(currentUser.getId(), newPasswordHash);
            System.out.println("Password updated successfully for user ID: " + currentUser.getId());
        } catch (Exception e) {
            System.err.println("Database update error while changing password: " + e.getMessage());
            throw new RuntimeException("Failed to change password: " + e.getMessage(), e);
        }
    }

    /**
     * 删除当前认证用户的账户（软删除）
     */
    @Transactional
    public void deleteCurrentUserAccount(String password) {
        User currentUser = getCurrentAuthenticatedUser();

        // 验证密码
        if (!passwordEncoder.matches(password, currentUser.getPasswordHash())) {
            throw new RuntimeException("Password is incorrect.");
        }

        try {
            userMapper.softDeleteById(currentUser.getId());
            System.out.println("User account soft deleted successfully for user ID: " + currentUser.getId());
        } catch (Exception e) {
            System.err.println("Database error while deleting user account: " + e.getMessage());
            throw new RuntimeException("Failed to delete user account: " + e.getMessage(), e);
        }
    }
}
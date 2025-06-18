package com.fy.schoolwall.admin.controller;

import com.fy.schoolwall.admin.dto.AdminUserDto;
import com.fy.schoolwall.admin.dto.BatchUserStatusRequest;
import com.fy.schoolwall.admin.service.AdminUserService;
import com.fy.schoolwall.common.enums.UserRole;
import com.fy.schoolwall.common.util.PaginationUtil;
import com.fy.schoolwall.user.dto.UserStatusRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * 获取所有用户列表（分页）
     * GET /admin/users?page=0&size=10&sort=createdAt&direction=DESC
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String role) {

        // 如果没有分页参数，返回所有用户（保持向后兼容）
        if (page == 0 && size == 10 && sort == null && enabled == null && role == null) {
            List<AdminUserDto> users = adminUserService.getAllUsers();
            return ResponseEntity.ok(users);
        }

        // 根据不同条件查询
        if (enabled != null) {
            List<AdminUserDto> users = adminUserService.getUsersByStatus(enabled);
            return ResponseEntity.ok(users);
        }

        if (role != null) {
            UserRole userRole = UserRole.fromCode(role.toUpperCase());
            List<AdminUserDto> users = adminUserService.getUsersByRole(userRole);
            return ResponseEntity.ok(users);
        }

        // 分页查询
        PaginationUtil.PageRequest pageRequest = PaginationUtil.validatePageRequest(page, size, sort, direction);
        PaginationUtil.PageResponse<AdminUserDto> pagedUsers = adminUserService.getAllUsers(pageRequest);
        return ResponseEntity.ok(pagedUsers);
    }

    /**
     * 获取用户详情
     * GET /admin/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserDto> getUserById(@PathVariable Long userId) {
        AdminUserDto user = adminUserService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * 管理员删除用户账户（硬删除）
     * DELETE /admin/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUserByAdmin(userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 管理员切换用户状态（启用/禁用）
     * PUT /admin/users/{userId}/status
     */
    @PutMapping("/{userId}/status")
    public ResponseEntity<Map<String, String>> toggleUserStatus(@PathVariable Long userId,
            @Valid @RequestBody UserStatusRequest statusRequest) {

        adminUserService.toggleUserStatus(userId, statusRequest.getEnabled());

        Map<String, String> response = new HashMap<>();
        String action = statusRequest.getEnabled() ? "enabled" : "disabled";
        response.put("message", "User " + action + " successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * 批量切换用户状态
     * PUT /admin/users/batch/status
     */
    @PutMapping("/batch/status")
    public ResponseEntity<Map<String, String>> batchToggleUserStatus(
            @Valid @RequestBody BatchUserStatusRequest request) {
        adminUserService.batchToggleUserStatus(request.getUserIds(), request.getEnabled());

        Map<String, String> response = new HashMap<>();
        String action = request.getEnabled() ? "enabled" : "disabled";
        response.put("message",
                "Batch status update (" + action + ") for " + request.getUserIds().size() + " users completed");
        response.put("status", action);
        response.put("count", String.valueOf(request.getUserIds().size()));
        return ResponseEntity.ok(response);
    }
}
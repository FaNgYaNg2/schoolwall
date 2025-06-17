package com.fy.schoolwall.auth.controller;

import com.fy.schoolwall.auth.dto.RegisterRequest;
import com.fy.schoolwall.auth.service.AuthService;
import com.fy.schoolwall.user.dto.UserProfileDto;
import com.fy.schoolwall.user.model.User; // 引入User实体
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // 确保使用 jakarta.validation

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerUser(registerRequest);
            return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/whoami")
    public ResponseEntity<UserProfileDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String
                        && "anonymousUser".equals(authentication.getPrincipal()))) {
            // principal 是 UserDetails 对象
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // 在实际项目中，你可能需要从数据库重新获取完整的 User 对象，
            // 或者 CustomUserDetailsService 返回包含更多信息的 UserDetails 实现
            User user = authService.getUserByUsername(userDetails.getUsername());
            if (user != null) {
                UserProfileDto dto = new UserProfileDto();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setAvatarUrl(user.getAvatarUrl());
                dto.setBio(user.getBio());
                dto.setRole(user.getRole());
                return ResponseEntity.ok(dto);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 未认证或匿名用户
    }
}
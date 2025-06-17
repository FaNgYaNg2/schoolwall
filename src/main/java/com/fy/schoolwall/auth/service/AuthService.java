package com.fy.schoolwall.auth.service;

import com.fy.schoolwall.auth.dto.RegisterRequest;
import com.fy.schoolwall.user.model.User;
import com.fy.schoolwall.user.repository.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (userMapper.findByUsername(registerRequest.getUsername()) != null) {
            throw new RuntimeException("Username already exists!");
        }
        if (userMapper.findByEmail(registerRequest.getEmail()) != null) {
            throw new RuntimeException("Email already exists!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword())); // 编码密码
        user.setRole("USER"); // 默认角色
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsEnabled(true);
        user.setIsLocked(false);

        userMapper.insert(user);
    }

    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}
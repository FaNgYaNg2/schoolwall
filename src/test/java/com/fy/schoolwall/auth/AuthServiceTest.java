package com.fy.schoolwall.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest {
    public static void main(String[] args) {
        // 创建一个 BCryptPasswordEncoder 实例，成本因子默认为 10
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = "123456";

        // 对原始密码进行哈希
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("原始密码: " + rawPassword);
        System.out.println("BCrypt 哈希值: " + encodedPassword);

        // 验证密码
        boolean isMatch = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("密码是否匹配: " + isMatch);
    }
}
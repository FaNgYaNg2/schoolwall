package com.fy.schoolwall.auth.security;

import com.fy.schoolwall.user.model.User;
import com.fy.schoolwall.user.repository.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections; // For simple role representation

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        // 构建 Spring Security 的 UserDetails 对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                user.getIsEnabled(), // enabled
                !user.getIsLocked(), // accountNonLocked
                true, // credentialsNonExpired
                true, // accountNonExpired
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())) // 添加角色
        );
    }
}
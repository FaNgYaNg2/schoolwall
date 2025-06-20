package com.fy.schoolwall.user.repository;

import com.fy.schoolwall.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findById(Long id);

    User findByUsername(String username);

    User findByEmail(String email); // 用于注册时检查邮箱是否存在

    void insert(User user);

    void update(User user); // 更新用户信息

    void updatePassword(Long userId, String passwordHash); // 更新密码

    void deleteById(Long id); // 硬删除

    void softDeleteById1(@Param("userId") Long userId);

    void softDeleteById(Long id); // 软删除（设置is_enabled为false）

    void updateUserStatus(Long userId, boolean enabled); // 更新用户状态

    List<User> findAllUsers(); // 管理员查看所有用户

    List<User> findUsersByStatus(boolean enabled); // 根据状态查找用户
}
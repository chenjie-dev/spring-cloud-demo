package com.example.user.service;

import com.example.common.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 创建用户
     */
    User createUser(User user);
    
    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);
    
    /**
     * 获取所有用户
     */
    List<User> getAllUsers();
    
    /**
     * 更新用户
     */
    User updateUser(User user);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Long id);
} 
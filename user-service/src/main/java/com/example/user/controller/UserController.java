package com.example.user.controller;

import com.example.common.entity.User;
import com.example.common.result.Result;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public Result<User> createUser(@Valid @RequestBody User user) {
        return Result.success(userService.createUser(user));
    }
    
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }
    
    @GetMapping
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.getAllUsers());
    }
    
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody User user) {
        user.setId(id);
        return Result.success(userService.updateUser(user));
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable("id") Long id) {
        return Result.success(userService.deleteUser(id));
    }
} 
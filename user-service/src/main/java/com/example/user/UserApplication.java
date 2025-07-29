package com.example.user;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 用户服务启动类
 */
@SpringBootApplication
@MapperScan("com.example.user.mapper")
public class UserApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
} 
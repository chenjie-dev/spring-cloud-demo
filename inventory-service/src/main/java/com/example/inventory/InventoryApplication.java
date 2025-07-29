package com.example.inventory;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 库存服务启动类
 */
@SpringBootApplication
@MapperScan("com.example.inventory.mapper")
public class InventoryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }
} 
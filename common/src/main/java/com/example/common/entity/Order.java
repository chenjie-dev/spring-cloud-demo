package com.example.common.entity;



import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
public class Order {
    
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long productId;
    
    private Integer quantity;
    
    private BigDecimal amount;
    
    private String status; // PENDING, PAID, CANCELLED
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
} 
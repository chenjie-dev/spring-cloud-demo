package com.example.order.controller;

import com.example.common.dto.OrderRequest;
import com.example.common.entity.Order;
import com.example.common.result.Result;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping
    public Result<Order> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        try {
            Order order = orderService.createOrder(orderRequest);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable("id") Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }
    
    @GetMapping
    public Result<List<Order>> getAllOrders() {
        return Result.success(orderService.getAllOrders());
    }
    
    @GetMapping("/user/{userId}")
    public Result<List<Order>> getOrdersByUserId(@PathVariable("userId") Long userId) {
        return Result.success(orderService.getOrdersByUserId(userId));
    }
    
    @PutMapping("/{id}/status")
    public Result<Order> updateOrderStatus(@PathVariable("id") Long id, @RequestParam String status) {
        try {
            Order order = orderService.updateOrderStatus(id, status);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable("id") Long id) {
        boolean success = orderService.cancelOrder(id);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("取消订单失败");
        }
    }
    

} 
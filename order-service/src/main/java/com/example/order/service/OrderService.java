package com.example.order.service;

import com.example.common.dto.OrderRequest;
import com.example.common.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单（包含分布式事务）
     */
    Order createOrder(OrderRequest orderRequest);
    
    /**
     * 根据ID获取订单
     */
    Order getOrderById(Long id);
    
    /**
     * 获取所有订单
     */
    List<Order> getAllOrders();
    
    /**
     * 根据用户ID获取订单
     */
    List<Order> getOrdersByUserId(Long userId);
    
    /**
     * 更新订单状态
     */
    Order updateOrderStatus(Long orderId, String status);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId);
} 
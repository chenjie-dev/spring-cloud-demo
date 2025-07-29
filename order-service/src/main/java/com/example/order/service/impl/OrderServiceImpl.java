package com.example.order.service.impl;

import com.example.common.dto.OrderRequest;
import com.example.common.entity.Order;
import com.example.common.entity.Product;
import com.example.common.entity.User;
import com.example.common.result.Result;
import com.example.order.mapper.OrderMapper;
import com.example.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        // 1. 生成订单号
        String orderNo = generateOrderNo();

        // 2. 验证用户是否存在
        User user = getUserById(orderRequest.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 获取商品信息并验证库存
        Product product = getProductById(orderRequest.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        if (product.getStock() < orderRequest.getQuantity()) {
            throw new RuntimeException("库存不足");
        }

        // 4. 计算订单金额
        BigDecimal amount = product.getPrice().multiply(new BigDecimal(orderRequest.getQuantity()));

        // 5. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(orderRequest.getUserId());
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order.setAmount(amount);
        order.setStatus("PENDING");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);

        // 6. 扣减库存（分布式事务）
        boolean stockDecreased = decreaseStock(orderRequest.getProductId(), orderRequest.getQuantity());
        if (!stockDecreased) {
            throw new RuntimeException("库存扣减失败");
        }

        // 7. 发送订单创建消息到Kafka
        try {
            String message = objectMapper.writeValueAsString(order);
            kafkaTemplate.send("order-created", orderNo, message);
        } catch (JsonProcessingException e) {
            // 记录日志，但不影响订单创建
            e.printStackTrace();
        }

        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderMapper.selectAll();
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        return order;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }

        // 更新订单状态
        order.setStatus("CANCELLED");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 恢复库存
        boolean stockIncreased = increaseStock(order.getProductId(), order.getQuantity());
        if (!stockIncreased) {
            // 记录日志，但不影响订单取消
            System.err.println("恢复库存失败，订单ID: " + orderId);
        }

        return true;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 获取用户信息
     */
    private User getUserById(Long userId) {
        String url = "http://localhost:8080/user-service/users/" + userId;

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Result.class)
                .map(result -> {
                    if (result.getCode() == 200) {
                        return objectMapper.convertValue(result.getData(), User.class);
                    }
                    return null;
                })
                .block();
    }

    /**
     * 获取商品信息
     */
    private Product getProductById(Long productId) {
        String url = "http://localhost:8080/inventory-service/inventory/products/" + productId;

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Result.class)
                .map(result -> {
                    if (result.getCode() == 200) {
                        return objectMapper.convertValue(result.getData(), Product.class);
                    }
                    return null;
                })
                .block();
    }

    /**
     * 扣减库存
     */
    private boolean decreaseStock(Long productId, Integer quantity) {
        try {
            System.out.println("开始扣减库存: productId=" + productId + ", quantity=" + quantity);

            // 使用简单的URL字符串
            String url = "http://localhost:8083/inventory/decrease-stock?productId=" + productId + "&quantity=" + quantity;
            System.out.println("请求URL: " + url);

            Result result = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Result.class)
                    .block();

            System.out.println("库存扣减响应: " + result);

            if (result != null && result.getCode() == 200) {
                System.out.println("库存扣减成功");
                return true;
            } else {
                System.out.println("库存扣减失败，响应: " + result);
                return false;
            }
        } catch (Exception e) {
            System.err.println("扣减库存异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 增加库存
     */
    private boolean increaseStock(Long productId, Integer quantity) {
        try {
            Result result = webClientBuilder.build()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .host("localhost")
                            .port(8080)
                            .path("/inventory-service/inventory/increase-stock")
                            .queryParam("productId", productId)
                            .queryParam("quantity", quantity)
                        .build())
                    .retrieve()
                    .bodyToMono(Result.class)
                    .block();
            
            return result != null && result.getCode() == 200;
        } catch (Exception e) {
            System.err.println("增加库存失败: " + e.getMessage());
            return false;
        }
    }
} 
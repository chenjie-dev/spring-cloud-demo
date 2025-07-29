package com.example.inventory.service;

import com.example.common.entity.Product;

import java.util.List;

/**
 * 库存服务接口
 */
public interface InventoryService {
    
    /**
     * 创建商品
     */
    Product createProduct(Product product);
    
    /**
     * 根据ID获取商品
     */
    Product getProductById(Long id);
    
    /**
     * 获取所有商品
     */
    List<Product> getAllProducts();
    
    /**
     * 更新商品
     */
    Product updateProduct(Product product);
    
    /**
     * 删除商品
     */
    boolean deleteProduct(Long id);
    
    /**
     * 扣减库存
     */
    boolean decreaseStock(Long productId, Integer quantity);
    
    /**
     * 增加库存
     */
    boolean increaseStock(Long productId, Integer quantity);
    
    /**
     * 检查库存是否充足
     */
    boolean checkStock(Long productId, Integer quantity);
} 
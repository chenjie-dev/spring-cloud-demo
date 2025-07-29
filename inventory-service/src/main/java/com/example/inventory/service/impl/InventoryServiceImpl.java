package com.example.inventory.service.impl;

import com.example.common.entity.Product;
import com.example.inventory.mapper.ProductMapper;
import com.example.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 库存服务实现类
 */
@Service
public class InventoryServiceImpl implements InventoryService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public Product createProduct(Product product) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);
        return product;
    }
    
    @Override
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productMapper.selectAll();
    }
    
    @Override
    @CacheEvict(value = "product", key = "#product.id")
    public Product updateProduct(Product product) {
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        return product;
    }
    
    @Override
    @CacheEvict(value = "product", key = "#id")
    public boolean deleteProduct(Long id) {
        return productMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional
    public boolean decreaseStock(Long productId, Integer quantity) {
        try {
            int result = productMapper.decreaseStock(productId, quantity);
            if (result > 0) {
                // 清除缓存
                try {
                    redisTemplate.delete("product::" + productId);
                } catch (Exception e) {
                    System.err.println("清除缓存失败: " + e.getMessage());
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("扣减库存失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean increaseStock(Long productId, Integer quantity) {
        try {
            int result = productMapper.increaseStock(productId, quantity);
            if (result > 0) {
                // 清除缓存
                try {
                    redisTemplate.delete("product::" + productId);
                } catch (Exception e) {
                    System.err.println("清除缓存失败: " + e.getMessage());
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("增加库存失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean checkStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        return product != null && product.getStock() >= quantity;
    }
} 
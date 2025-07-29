package com.example.inventory.controller;

import com.example.common.entity.Product;
import com.example.common.result.Result;
import com.example.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存控制器
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);
    @Autowired
    private InventoryService inventoryService;
    
    @PostMapping("/products")
    public Result<Product> createProduct(@Valid @RequestBody Product product) {
        return Result.success(inventoryService.createProduct(product));
    }
    
    @GetMapping("/products/{id}")
    public Result<Product> getProductById(@PathVariable("id") Long id) {
        Product product = inventoryService.getProductById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }
    
    @GetMapping("/products")
    public Result<List<Product>> getAllProducts() {
        return Result.success(inventoryService.getAllProducts());
    }
    
    @PutMapping("/products/{id}")
    public Result<Product> updateProduct(@PathVariable("id") Long id, @Valid @RequestBody Product product) {
        product.setId(id);
        return Result.success(inventoryService.updateProduct(product));
    }
    
    @DeleteMapping("/products/{id}")
    public Result<Boolean> deleteProduct(@PathVariable("id") Long id) {
        return Result.success(inventoryService.deleteProduct(id));
    }
    
    @PostMapping("/decrease-stock")
    public Result<Boolean> decreaseStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity) {
        log.info("Decrease stock productId={}, quantity={}", productId, quantity);
        boolean success = inventoryService.decreaseStock(productId, quantity);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("库存不足或扣减失败");
        }
    }
    
    @PostMapping("/increase-stock")
    public Result<Boolean> increaseStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity) {
        log.info("Increase stock productId={}, quantity={}", productId, quantity);
        boolean success = inventoryService.increaseStock(productId, quantity);
        if (success) {
            return Result.success(true);
        } else {
            return Result.error("增加库存失败");
        }
    }
    
    @GetMapping("/check-stock")
    public Result<Boolean> checkStock(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity) {
        boolean available = inventoryService.checkStock(productId, quantity);
        return Result.success(available);
    }
} 
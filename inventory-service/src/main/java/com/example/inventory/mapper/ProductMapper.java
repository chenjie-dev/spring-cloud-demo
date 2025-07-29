package com.example.inventory.mapper;

import com.example.common.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 商品Mapper
 */
@Mapper
@Repository
public interface ProductMapper {
    
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product selectById(Long id);
    
    @Select("SELECT * FROM products")
    List<Product> selectAll();
    
    @Insert("INSERT INTO products (name, description, price, stock, create_time, update_time) VALUES (#{name}, #{description}, #{price}, #{stock}, #{createTime}, #{updateTime})")
    int insert(Product product);
    
    @Update("UPDATE products SET name = #{name}, description = #{description}, price = #{price}, stock = #{stock}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(Product product);
    
    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 扣减库存
     */
    @Update("UPDATE products SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 增加库存
     */
    @Update("UPDATE products SET stock = stock + #{quantity} WHERE id = #{productId}")
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
} 
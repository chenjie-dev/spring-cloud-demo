package com.example.order.mapper;

import com.example.common.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 订单Mapper
 */
@Mapper
@Repository
public interface OrderMapper {
    
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order selectById(Long id);
    
    @Select("SELECT * FROM orders")
    List<Order> selectAll();
    
    @Select("SELECT * FROM orders WHERE user_id = #{userId}")
    List<Order> selectByUserId(Long userId);
    
    @Insert("INSERT INTO orders (order_no, user_id, product_id, quantity, amount, status, create_time, update_time) VALUES (#{orderNo}, #{userId}, #{productId}, #{quantity}, #{amount}, #{status}, #{createTime}, #{updateTime})")
    int insert(Order order);
    
    @Update("UPDATE orders SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(Order order);
    
    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);
} 
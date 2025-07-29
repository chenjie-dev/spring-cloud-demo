package com.example.user.mapper;

import com.example.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
@Repository
public interface UserMapper {
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectById(Long id);
    
    @Select("SELECT * FROM users")
    List<User> selectAll();
    
    @Insert("INSERT INTO users (username, email, phone, address, create_time, update_time) VALUES (#{username}, #{email}, #{phone}, #{address}, #{createTime}, #{updateTime})")
    int insert(User user);
    
    @Update("UPDATE users SET username = #{username}, email = #{email}, phone = #{phone}, address = #{address}, update_time = #{updateTime} WHERE id = #{id}")
    int updateById(User user);
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);
} 
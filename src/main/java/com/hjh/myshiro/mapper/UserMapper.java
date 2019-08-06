package com.hjh.myshiro.mapper;

import com.hjh.myshiro.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface UserMapper {
    int insert(User record);

    int insertSelective(User record);

    List<User> getUserByUsername(String username);

    void update(User user);

    List<User> list();

    User getUserById(long id);

    void deleteUserById(long id);
}
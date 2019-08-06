package com.hjh.myshiro.mapper;

import com.hjh.myshiro.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface UserRoleMapper {
    int insert(UserRole record);

    int insertSelective(UserRole record);

    List<UserRole> getUserRoleByUid(Long id);

    void deleteUserRoleById(Long id);
}
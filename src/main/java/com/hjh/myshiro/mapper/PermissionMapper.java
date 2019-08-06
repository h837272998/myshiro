package com.hjh.myshiro.mapper;

import com.hjh.myshiro.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper {
    int insert(Permission record);

    int insertSelective(Permission record);

    Permission getPermissionById(Long pid);

    List<Permission> list();

    void update(Permission permission);

    void delete(Long id);
}
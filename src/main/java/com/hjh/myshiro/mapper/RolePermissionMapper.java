package com.hjh.myshiro.mapper;

import com.hjh.myshiro.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface RolePermissionMapper {
    int insert(RolePermission record);

    int insertSelective(RolePermission record);

    List<RolePermission> getRolePermissionByRid(Long id);

    void deleteRolePermissionById(Long id);
}
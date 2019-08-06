package com.hjh.myshiro.mapper;

import com.hjh.myshiro.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    int insert(Role record);

    int insertSelective(Role record);

    Role getRoleById(Long rid);

    List<Role> list();

    void delete(Long id);

    void update(Role role);
}
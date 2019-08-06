package com.hjh.myshiro.service.impl;

import com.hjh.myshiro.entity.Role;
import com.hjh.myshiro.entity.RolePermission;
import com.hjh.myshiro.mapper.RolePermissionMapper;
import com.hjh.myshiro.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

	@Autowired
	RolePermissionMapper rolePermissionMapper;


	@Override
	public void setPermissions(Role role, long[] permissionIds) {
		// 删除当前角色所有的权限
		List<RolePermission> rps = rolePermissionMapper.getRolePermissionByRid(role.getId());
		for (RolePermission rolePermission : rps)
			rolePermissionMapper.deleteRolePermissionById(rolePermission.getId());

		// 设置新的权限关系
		if (null != permissionIds)
			for (long pid : permissionIds) {
				RolePermission rolePermission = new RolePermission();
				rolePermission.setPid(pid);
				rolePermission.setRid(role.getId());
				rolePermissionMapper.insert(rolePermission);
			}
	}

	@Override
	public void deleteByRole(long roleId) {

	}

	@Override
	public void deleteByPermission(long permissionId) {

	}
}
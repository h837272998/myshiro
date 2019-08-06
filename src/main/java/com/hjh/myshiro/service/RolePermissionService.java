package com.hjh.myshiro.service;


import com.hjh.myshiro.entity.Role;

public interface RolePermissionService {
	public void setPermissions(Role role, long[] permissionIds);

	public void deleteByRole(long roleId);

	public void deleteByPermission(long permissionId);
}
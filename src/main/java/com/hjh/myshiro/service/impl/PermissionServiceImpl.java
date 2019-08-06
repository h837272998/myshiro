package com.hjh.myshiro.service.impl;

import com.hjh.myshiro.entity.Permission;
import com.hjh.myshiro.entity.Role;
import com.hjh.myshiro.entity.RolePermission;
import com.hjh.myshiro.mapper.PermissionMapper;
import com.hjh.myshiro.mapper.RolePermissionMapper;
import com.hjh.myshiro.service.PermissionService;
import com.hjh.myshiro.service.RoleService;
import com.hjh.myshiro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	PermissionMapper permissionMapper;
	@Autowired
	UserService userService;
	@Autowired
	RoleService roleService;
	@Autowired
	RolePermissionMapper rolePermissionMapper;


	@Override
	public Set<String> listPermissions(String userName) {
		Set<String> result = new HashSet<>();
		List<Role> roles = roleService.listRoles(userName);

		List<RolePermission> rolePermissions = new ArrayList<>();
		for (Role role : roles) {
			List<RolePermission> rps = rolePermissionMapper.getRolePermissionByRid(role.getId());
			rolePermissions.addAll(rps);
		}
		for (RolePermission rolePermission : rolePermissions) {
			Permission p = permissionMapper.getPermissionById(rolePermission.getPid());
			result.add(p.getName());
		}

		return result;
	}

	@Override
	public List<Permission> list() {

		return permissionMapper.list();
	}

	@Override
	public void add(Permission permission) {
		permissionMapper.insertSelective(permission);
	}

	@Override
	public void delete(Long id) {
		permissionMapper.delete(id);
	}

	@Override
	public Permission get(Long id) {
		return permissionMapper.getPermissionById(id);
	}

	@Override
	public void update(Permission permission) {
		permissionMapper.update(permission);
	}

	@Override
	public List<Permission> list(Role role) {
		List<Permission> result = new ArrayList<>();
		List<RolePermission> rps = rolePermissionMapper.getRolePermissionByRid(role.getId());
		for (RolePermission rolePermission : rps) {
			result.add(permissionMapper.getPermissionById(rolePermission.getPid()));
		}
		return result;
	}

	@Override
	public boolean needInterceptor(String requestURI) {
		List<Permission> permissions = list();
		for (Permission permission : permissions) {
			if (permission.getUrl().equals(requestURI)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<String> listPermissionURLs(String userName) {
		Set<String> result = new HashSet<>();
		List<Role> roles = roleService.listRoles(userName);
		List<RolePermission> rolePermissions = new ArrayList<>();
		for (Role role : roles) {
			List<RolePermission> rolePermissions1= rolePermissionMapper.getRolePermissionByRid(role.getId());
			rolePermissions.addAll(rolePermissions1);
		}

		for (RolePermission rolePermission : rolePermissions) {
			Permission p = permissionMapper.getPermissionById(rolePermission.getPid());
			result.add(p.getUrl());
		}
		return result;
	}
}
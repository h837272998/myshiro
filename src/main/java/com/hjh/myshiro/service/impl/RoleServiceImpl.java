package com.hjh.myshiro.service.impl;


import com.hjh.myshiro.entity.Role;
import com.hjh.myshiro.entity.User;
import com.hjh.myshiro.entity.UserRole;
import com.hjh.myshiro.mapper.RoleMapper;
import com.hjh.myshiro.mapper.UserRoleMapper;
import com.hjh.myshiro.service.RoleService;
import com.hjh.myshiro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
	@Autowired
	RoleMapper roleMapper;
	@Autowired
	UserRoleMapper userRoleMapper;
	@Autowired
	UserService userService;


	@Override
	public Set<String> listRoleNames(String userName) {
		Set<String> result = new HashSet<>();
		List<Role> roles = listRoles(userName);
		for (Role role : roles) {
			result.add(role.getName());
		}
		return result;
	}

	@Override
	public List<Role> listRoles(String userName) {
		List<Role> roles = new ArrayList<>();
		List<User> users = userService.getUserByUsername(userName);
		if (users.size()==0){
			return roles;
		}
		roles = listRoles(users.get(0));
		return roles;
	}

	@Override
	public List<Role> listRoles(User user) {
		List<Role> roles = new ArrayList<>();
		List<UserRole> userRoles = userRoleMapper.getUserRoleByUid(user.getId());
		for (UserRole userRole : userRoles) {
			Role role = roleMapper.getRoleById(userRole.getRid());
			roles.add(role);
		}
		return roles;
	}

	@Override
	public List<Role> list() {
		return roleMapper.list();
	}

	@Override
	public void add(Role role) {
		roleMapper.insertSelective(role);
	}

	@Override
	public void delete(Long id) {
		roleMapper.delete(id);
	}

	@Override
	public Role getRoleById(Long id) {
		return roleMapper.getRoleById(id);
	}

	@Override
	public void update(Role role) {
		roleMapper.update(role);
	}
}
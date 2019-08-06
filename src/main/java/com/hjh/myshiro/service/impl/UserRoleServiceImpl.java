package com.hjh.myshiro.service.impl;


import com.hjh.myshiro.entity.User;
import com.hjh.myshiro.entity.UserRole;
import com.hjh.myshiro.mapper.UserRoleMapper;
import com.hjh.myshiro.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	UserRoleMapper userRoleMapper;


	@Override
	public void setRoles(User user, long[] roleIds) {
		List<UserRole> urs = userRoleMapper.getUserRoleByUid(user.getId());
		for (UserRole userRole : urs)
			userRoleMapper.deleteUserRoleById(userRole.getId());

		// 设置新的角色关系
		if (null != roleIds)
			for (long rid : roleIds) {
				UserRole userRole = new UserRole();
				userRole.setRid(rid);
				userRole.setUid(user.getId());
				userRoleMapper.insert(userRole);
			}
	}

	@Override
	public void deleteByUser(long userId) {

	}

	@Override
	public void deleteByRole(long roleId) {

	}
}
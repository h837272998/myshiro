package com.hjh.myshiro.service;

import com.hjh.myshiro.entity.User;

public interface UserRoleService {

	public void setRoles(User user, long[] roleIds);

	public void deleteByUser(long userId);

	public void deleteByRole(long roleId);

}
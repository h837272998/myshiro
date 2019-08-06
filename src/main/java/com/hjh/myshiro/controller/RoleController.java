package com.hjh.myshiro.controller;

import com.hjh.myshiro.entity.Permission;
import com.hjh.myshiro.entity.Role;
import com.hjh.myshiro.service.PermissionService;
import com.hjh.myshiro.service.RolePermissionService;
import com.hjh.myshiro.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-08-05 16:55
 */
@Controller
@RequestMapping("admin")
public class RoleController {

    @Autowired
    RoleService roleService;
    @Autowired
    RolePermissionService rolePermissionService;
    @Autowired
    PermissionService permissionService;

    @RequestMapping("listRole")
    public String list(Model model) {
        List<Role> rs = roleService.list();
        model.addAttribute("rs", rs);

        Map<Role, List<Permission>> role_permissions = new HashMap<>();

        for (Role role : rs) {
            List<Permission> ps = permissionService.list(role);
            role_permissions.put(role, ps);
        }
        model.addAttribute("role_permissions", role_permissions);

        return "listRole";
    }

    @RequestMapping("editRole")
    public String list(Model model, long id) {
        Role role = roleService.getRoleById(id);
        model.addAttribute("role", role);

        List<Permission> ps = permissionService.list();
        model.addAttribute("ps", ps);

        List<Permission> currentPermissions = permissionService.list(role);
        List<Long> tCurrentPermissions = new ArrayList<>();
        for (Permission permission:currentPermissions) {
            tCurrentPermissions.add(permission.getId());
        }
        model.addAttribute("currentPermissions", tCurrentPermissions);


        return "editRole";
    }

    @RequestMapping("updateRole")
    public String update(Role role, long[] permissionIds) {
        rolePermissionService.setPermissions(role, permissionIds);
        roleService.update(role);
        return "redirect:listRole";
    }

    @RequestMapping("addRole")
    public String list(Model model, Role role) {
        roleService.add(role);
        return "redirect:listRole";
    }

    @RequestMapping("deleteRole")
    public String delete(Model model, long id) {
        roleService.delete(id);
        return "redirect:listRole";
    }
}

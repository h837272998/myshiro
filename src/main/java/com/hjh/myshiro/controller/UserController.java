package com.hjh.myshiro.controller;

import com.hjh.myshiro.entity.Role;
import com.hjh.myshiro.entity.User;
import com.hjh.myshiro.entity.UserOnlineBo;
import com.hjh.myshiro.service.RoleService;
import com.hjh.myshiro.service.UserRoleService;
import com.hjh.myshiro.service.UserService;
import com.hjh.myshiro.utils.ResultBean;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-08-05 15:38
 */
@Controller
@RequestMapping("admin")
public class UserController {

    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @GetMapping("listUser")
    public String list(Model model){
        List<User> us = userService.list();
        model.addAttribute("us", us);
        Map<User, List<Role>> user_roles = new HashMap<>();
        for (User user : us) {
            List<Role> roles = roleService.listRoles(user);
            user_roles.put(user, roles);
        }
        model.addAttribute("user_roles", user_roles);
        return "listUser";
    }

    @GetMapping("editUser")
    public String edit(Model model,long id){
        List<Role> rs = roleService.list();
        model.addAttribute("rs", rs);
        User user = userService.getUserById(id);
        model.addAttribute("user", user);

        List<Role> roles = roleService.listRoles(user);
        List<Long> re = new ArrayList<>();
        for (Role role:roles) {
            re.add(role.getId());
        }
        model.addAttribute("currentRoles", re);
        return "editUser";
    }

    @RequestMapping("deleteUser")
    public String delete(Model model, long id) {
        userService.deleteUserById(id);
        return "redirect:listUser";
    }

    @RequestMapping("updateUser")
    public String update(User user, long[] roleIds) {
        userRoleService.setRoles(user, roleIds);
        String password = user.getPswd();
        // 如果在修改的时候没有设置密码，就表示不改动密码
        if (user.getPswd().length() != 0) {
            String salt = new SecureRandomNumberGenerator().nextBytes().toString();
            int times = 2;
            String algorithmName = "md5";
            String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();
            user.setSalt(salt);
            user.setPswd(encodedPassword);
        } else
            user.setPswd(null);

        userService.update(user);

        return "redirect:listUser";

    }

    @RequestMapping("addUser")
    public String add(Model model, String name, String password) {

        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithmName = "md5";

        String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();

        User u = new User();
        u.setUsername(name);
        u.setPswd(encodedPassword);
        u.setSalt(salt);
        userService.insertSelective(u);

        return "redirect:listUser";
    }

    @RequestMapping("listOnline")
    public String getListOnline(Model model){
        List<UserOnlineBo> userOnlineBos = userService.onlineList();
        model.addAttribute("userOnline",userOnlineBos);
        return "listOnline";
    }

    @RequestMapping("tOut")
    public String tOut(String  sessionId){
        userService.tOut(sessionId);
        return "success";
    }

    @PostMapping("test")
    @ResponseBody
    public ResultBean test(){

        return new ResultBean();
    }

}

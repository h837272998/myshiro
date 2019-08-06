package com.hjh.myshiro.realm;

import com.hjh.myshiro.entity.User;
import com.hjh.myshiro.service.PermissionService;
import com.hjh.myshiro.service.RoleService;
import com.hjh.myshiro.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-08-01 15:18
 */
@Slf4j
public class DatabaseRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    RoleService roleService;

    /**
     * @Description:授权验证
     * @Author: HJH
     * @Date: 2019-08-01 15:20
     * @Param: [principals]
     * @Return: org.apache.shiro.authz.AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("---- HJH ---- doGetAuthorizationInfo 授权验证调用...");
        // 能进入到这里，表示账号已经通过验证了
        String userName = (String) principals.getPrimaryPrincipal();
        // 通过service获取角色和权限
        Set<String> permissions = permissionService.listPermissions(userName);
        Set<String> roles = roleService.listRoleNames(userName);
        // 授权对象
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        // 把通过service获取到的角色和权限放进去
        s.setStringPermissions(permissions);
        s.setRoles(roles);
        return s;
    }

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * @Description:登录验证
     * @Author: HJH
     * @Date: 2019-08-01 15:20
     * @Param: [token]
     * @Return: org.apache.shiro.authc.AuthenticationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("---- HJH ---- doGetAuthenticationInfo 身份验证调用...");
        UsernamePasswordToken token1 = (UsernamePasswordToken) token;
        String username = token1.getUsername();
        List<User> users = userService.getUserByUsername(username);

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.increment("count_"+username, 1);
        //计数大于5时，设置用户被锁定一小时
        if(Integer.parseInt(operations.get("count_"+username))>=5){
            operations.set("lock_"+username, "LOCK");
            stringRedisTemplate.expire("lock_"+username, 1, TimeUnit.HOURS);
        }
        if ("LOCK".equals(operations.get("lock_"+username))){
            throw new DisabledAccountException("由于密码输入错误次数大于5次，帐号已经禁止登录！");
        }
        User user = null;
        if (users.size()!=0){
            user = users.get(0);
        }
        if (null==user){
            throw new AccountException("账号不存在...");
        }else if(user.getStatus()==0){
            throw new AccountException("账号已被封禁...");
        }else{
            user.setLastLoginTime(new Date());
            userService.update(user);
        }
        // 认证信息里存放账号密码, getName() 是当前Realm的继承方法,通常返回当前类名 :databaseRealm
        // 盐也放进去
        // 这样通过applicationContext-shiro.xml里配置的 HashedCredentialsMatcher 进行自动校验
        SimpleAuthenticationInfo a = new SimpleAuthenticationInfo(username, user.getPswd(), ByteSource.Util.bytes(user.getSalt()),
                getName());

        return a;
    }
}

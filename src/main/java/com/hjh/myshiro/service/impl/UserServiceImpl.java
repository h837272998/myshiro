package com.hjh.myshiro.service.impl;


import com.hjh.myshiro.entity.User;
import com.hjh.myshiro.entity.UserOnlineBo;
import com.hjh.myshiro.mapper.UserMapper;
import com.hjh.myshiro.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;

import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-08-01 15:30
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;


    @Override
    public int insert(User record) {
        return userMapper.insert(record);
    }

    @Override
    public int insertSelective(User record) {
        return userMapper.insertSelective(record);
    }

    @Override
    public List<User> getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);

    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override
    public User getUserById(long id) {
        return userMapper.getUserById(id);
    }

    @Override
    public void deleteUserById(long id) {
        userMapper.deleteUserById(id);
    }



    //这里態使用redisSessionDao。会造成session的混乱。从sessionManageer取出redisSessionDao
    @Autowired
    DefaultWebSessionManager defaultWebSessionManager;

    @Override
    public List<UserOnlineBo> onlineList() {
        // 因为用redis实现了shiro的session的Dao,而且还采用了shiro+redis这个插件
        // 所以从spring容器中获取redisSessionDAO
        // 来获取session列表.
//        SessionDAO redisSessionDAO = ((DefaultWebSessionManager)defaultWebSecurityManager.getSessionManager()).getSessionDAO();
        SessionDAO redisSessionDAO = defaultWebSessionManager.getSessionDAO();
        Collection<Session> sessions = redisSessionDAO.getActiveSessions();
        Iterator<Session> it = sessions.iterator();
        List<UserOnlineBo> onlineUserList = new ArrayList<UserOnlineBo>();
        // 遍历session
        while (it.hasNext()) {
            // 这是shiro已经存入session的
            // 现在直接取就是了
            Session session = it.next();
            // 如果被标记为踢出就不显示
            Object obj = session.getAttribute("kickout");
            if (obj != null)
                continue;
            UserOnlineBo onlineUser = getUserOnlineBySession(session);
            onlineUserList.add(onlineUser);
        }
        return onlineUserList;
    }

    @Override
    public UserOnlineBo getUserOnlineBySession(Session session) {
        //获取session登录信息。
        Object obj = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        if(null == obj){
            return null;
        }
        //确保是 SimplePrincipalCollection对象。
        if(obj instanceof SimplePrincipalCollection){
            SimplePrincipalCollection spc = (SimplePrincipalCollection)obj;
            /**
             * 获取用户登录的，@link SampleRealm.doGetAuthenticationInfo(...)方法中
             * return new SimpleAuthenticationInfo(user,user.getPswd(), getName());的user 对象。
             */
            obj = spc.getPrimaryPrincipal();
            if(null != obj){
                //存储session + user 综合信息
                 User user = new User();
                user.setUsername(obj.toString());
                UserOnlineBo userBo = new UserOnlineBo((User)user);
                //最后一次和系统交互的时间
                userBo.setLastAccess(session.getLastAccessTime());
                //主机的ip地址
                userBo.setHost(session.getHost());
                //session ID
                userBo.setSessionId(session.getId().toString());
                //session最后一次与系统交互的时间
                userBo.setLastLoginTime(session.getLastAccessTime());
                //回话到期 ttl(ms)
                userBo.setTimeout(session.getTimeout());
                //session创建时间
                userBo.setStartTime(session.getStartTimestamp());
                //是否踢出
                userBo.setSessionStatus(false);
                return userBo;
            }
        }
        return null;
    }

    @Override
    public void tOut(String sessionId) {
        Session session = defaultWebSessionManager.getSession(new DefaultSessionKey(sessionId));
        session.setAttribute("kickout",true);
    }
}

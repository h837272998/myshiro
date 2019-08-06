package com.hjh.myshiro.service;

import com.hjh.myshiro.entity.User;
import com.hjh.myshiro.entity.UserOnlineBo;
import com.hjh.myshiro.mapper.UserMapper;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-08-01 15:25
 */
public interface UserService {

    int insert(User record);

    int insertSelective(User record);

    public List<User> getUserByUsername(String username);

    void update(User user);

    List<User> list();

    User getUserById(long id);

    void deleteUserById(long id);

    List<UserOnlineBo> onlineList();

    UserOnlineBo getUserOnlineBySession(Session session);

    void tOut(String sessionId);
}

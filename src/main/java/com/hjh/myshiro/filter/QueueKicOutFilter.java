package com.hjh.myshiro.filter;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.hjh.myshiro.entity.User;
import com.hjh.myshiro.utils.ResultBean;
import lombok.ToString;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @Description:AccessControlFilter的超类是PathMatchingFilter
 * @Author: HJH
 * @Date: 2019-08-06 9:33
 */
public class QueueKicOutFilter extends AccessControlFilter {
    private String kickoutUrl; //踢出后到的地址
    private boolean kickoutAfter = false; //踢出之前登录的/之后登录的用户 默认踢出之前登录的用户
    private int maxSession = 1; //同一个帐号最大会话数 默认1

    private SessionManager sessionManager;
    private Cache<String, Deque<Serializable>> cache;

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }


    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    //获取redis 缓存
    public void setCacheManager(CacheManager cacheManager) {
        //获取name的缓存。如果缓存不存在则创建该name的缓存
        this.cache = cacheManager.getCache("shiro_redis_cache");
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * @Description:表示是否允许访问；mappedValue就是[urls]配置中拦截器参数部分，如果允许访问返回true，否则false；
     * @Author: HJH
     * @Date: 2019-08-06 9:51
     * @Param: [request, response, mappedValue]
     * @Return: boolean
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    /**
     * @Description:表示当前访问链接时是否已经处理了；如果返回true表示需要继续处理拦截链；如果返回false表示该拦截器实例已经处理了，将直接返回即可。
     * @Author: HJH
     * @Date: 2019-08-06 9:52
     * @Param: [request, response]
     * @Return: boolean
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        //没有登录直接返回。继续处理链
        //
        if (!subject.isAuthenticated()&&!subject.isRemembered()){
            return true;
        }

        Session session = subject.getSession();
        String username = subject.getPrincipal().toString();
        Serializable sessionId = session.getId();

        //获取当前用户队列，没有就初始化
        Deque<Serializable> deque = cache.get(username);
        if(deque==null){
            deque = new LinkedList<Serializable>();
        }

        //队列没有改session且用户没有被标记踢出
        if (!deque.contains(sessionId)&&session.getAttribute("kickout") == null){
            //将sessionId存入队列
            deque.push(sessionId);
            //将用户的sessionId队列缓存
            cache.put(username, deque);
        }

        //踢人
        while (deque.size()>maxSession){
            Serializable kickoutSessionId = null;
            if(kickoutAfter) { //如果踢出后者
                kickoutSessionId = deque.removeFirst();
            } else { //否则踢出前者
                kickoutSessionId = deque.removeLast();
            }
            //踢出后再更新下缓存队列
            cache.put(username, deque);

            //获取被踢出的sessionId的session对象
            Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
            if(kickoutSession != null) {
                //设置会话的kickout属性表示踢出了
                kickoutSession.setAttribute("kickout", true);
            }
        }

        //如果被踢出了，直接退出，重定向到踢出后的地址
        if ((Boolean)session.getAttribute("kickout")!=null&&(Boolean)session.getAttribute("kickout") == true) {
            //会话被踢出了
            try {
                //退出登录
                subject.logout();
            } catch (Exception e) { //ignore
            }
            saveRequest(request);
            //重定向或ajax请求的返回
            if("XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest)request).getHeader("X-Requested-With"))){
                ResultBean resultBean = new ResultBean("该账号已被迫下线...");
                response.setCharacterEncoding("utf-8");
                response.getWriter().println(JSONUtil.toJsonStr(resultBean));
            }else{
                WebUtils.issueRedirect(request, response, kickoutUrl);
            }

            return false;
        }

        return true;
    }
}

package com.hjh.myshiro.config;

import com.hjh.myshiro.entity.Permission;
import com.hjh.myshiro.filter.QueueKicOutFilter;
import com.hjh.myshiro.service.PermissionService;
import com.hjh.myshiro.utils.MyFilterChain;
import com.hjh.myshiro.filter.URLPathMatchingFilter;
import com.hjh.myshiro.realm.DatabaseRealm;
import com.hjh.myshiro.service.ShiroService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-08-01 14:57
 */

@Configuration
@Slf4j
public class ShiroConfiguration {
    /**
     * @Description:spring-shiro配置
     * @Author: HJH
     * @Date: 2019-08-03 14:10
     * @Param: []
     * @Return: org.apache.shiro.spring.LifecycleBeanPostProcessor
     */
    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /*
     * @Description:注入拦截器配置。
     * @Author: HJH
     * @Date: 2019-08-05 10:50
     */
    @Autowired
    MyFilterChain filterChain;

    @Autowired
    PermissionService permissionService;


    /**
     * @Description:ShiroFilterFactoryBean入口。拦截需要安全机制的URL，进行对应的控制
     * @Author: HJH
     * @Date: 2019-08-01 14:59
     * @Param: [securityManager]
     * @Return: org.apache.shiro.spring.web.ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置secutityManageer
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //设置登录url ，默认是web工程根目录的login
        shiroFilterFactoryBean.setLoginUrl("/login");
        //登录成功后跳转的页面
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //未授权界面
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");
        //拦截器
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //自定义拦截器
        Map<String, Filter> customisedFilter = new HashMap<>();
        //URl权限管理 取消，存在许多缺点和不足。但是也是一种添加自定义过滤器的方法。。
        // 原因：使用redis 无法实现url缓存。将其修改到realm权限验证。且使用user，拦截没有登录。使用server实现动态添加权限拦截。这样更好。给特定的链接添加权限认证，其他链接需要登录。
//        customisedFilter.put("url", getURLPathMatchingFilter());
        customisedFilter.put("kickout",queueKicOutFilter());

        //配置映射关系 按顺序判断。anon匿名访问，authc 认证通过才可以访问
        for (String key:filterChain.getFilterchain().keySet()){
            filterChainDefinitionMap.put(key,filterChain.getFilterchain().get(key));
        }
        List<Permission> permissions = permissionService.list();
        for (Permission permission : permissions) {
            filterChainDefinitionMap.put(permission.getUrl(), "perms[" + permission.getName() + "],kickout");
        }
        filterChainDefinitionMap.put("/**", "kickout,user");

        shiroFilterFactoryBean.setFilters(customisedFilter);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        log.info("----  HJH  ---- ShiroFilterFactoryBean注入...");
        return shiroFilterFactoryBean;
    }

//    @Bean
    /**
     * @Description:没有用@Bean管理起来。 原因是Shiro的bug, 这个也是过滤器，ShiroFilterFactoryBean 也是过滤器，
     * 当他们都出现的时候，默认的什么anno,authc,logout过滤器就失效了。所以不能把他声明为@Bean。
     * @Author: HJH
     * @Date: 2019-08-03 15:08
     * @Param: []
     * @Return: javax.servlet.Filter
     */
    public Filter getURLPathMatchingFilter() {
        return new URLPathMatchingFilter();
    }


    /**
     * @Description:获得securityManager
     * @Author: HJH
     * @Date: 2019-08-01 15:16
     * @Param: []
     * @Return: org.apache.shiro.mgt.SecurityManager
     */
    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
        //设置realm
        defaultSecurityManager.setRealm(shiroRealm());
        // 自定义缓存实现 使用redis
        defaultSecurityManager.setCacheManager(cacheManager());
        // 自定义session管理 使用redis
        defaultSecurityManager.setSessionManager(webSessionManager());
        //记住我
        defaultSecurityManager.setRememberMeManager(rememberMeManager());
        return defaultSecurityManager;
    }

    /**
     * @Description:获得Realm
     * @Author: HJH
     * @Date: 2019-08-01 15:16
     * @Param: []
     * @Return: org.apache.shiro.realm.Realm
     */
    @Bean
    public Realm shiroRealm() {
        DatabaseRealm databaseRealm = new DatabaseRealm();
        databaseRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return  databaseRealm;
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     *  所以我们需要修改下doGetAuthenticationInfo中的代码;
     * ）
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }


    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    /**
     * @Description:配置redisManager
     * @Author: HJH
     * @Date: 2019-08-04 23:03
     * @Param: []
     * @Return: org.crazycake.shiro.RedisManager
     */
    public RedisManager redisManager(){
        //使用默认 host=127.0.0.1:6379  timeout=2000
        RedisManager redisManager = new RedisManager();
        return redisManager;
    }

    /**
     * @Description:配置cacheManager 使用redisManager
     * @Author: HJH
     * @Date: 2019-08-04 23:04
     * @Param: []
     * @Return: org.crazycake.shiro.RedisCacheManager
     */
    public RedisCacheManager cacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    /**
     * @Description:配置redisSessionDAO，shiro sessionDao层的实现 通过redis
     * @Author: HJH
     * @Date: 2019-08-04 23:07
     * @Param: []
     * @Return: org.crazycake.shiro.RedisSessionDAO
     */
//    @Bean
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    /**
     * @Description:shiro session管理
     * @Author: HJH
     * @Date: 2019-08-04 23:10
     * @Param: []
     * @Return: org.apache.shiro.web.session.mgt.DefaultWebSessionManager
     */
    @Bean
    public DefaultWebSessionManager webSessionManager(){
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setSessionDAO(redisSessionDAO());
        return defaultWebSessionManager;
    }

    /**
     * @Description:Cookie，SimpleCookie对象
     * @Author: HJH
     * @Date: 2019-08-05 11:01
     * @Param: []
     * @Return: org.apache.shiro.web.servlet.SimpleCookie
     */
    public SimpleCookie rememberMeCookie(){
        //cookie名称。也对应着前端的checkbox的name
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //生效时间 单位s
        simpleCookie.setMaxAge(7*24*60*60);
        return simpleCookie;
    }

    /**
     * @Description:CookierememberMeManager 对象
     * @Author: HJH
     * @Date: 2019-08-05 11:04
     * @Param: []
     * @Return: org.apache.shiro.web.mgt.CookieRememberMeManager
     */
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("3AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

    public QueueKicOutFilter queueKicOutFilter(){
        QueueKicOutFilter queueKicOutFilter = new QueueKicOutFilter();
        //使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
        //这里我们还是用之前shiro使用的redisManager()实现的cacheManager()缓存管理
        //也可以重新另写一个，重新配置缓存时间之类的自定义缓存属性
        queueKicOutFilter.setCacheManager(cacheManager());
        //用于根据会话ID，获取会话进行踢出操作的；
        queueKicOutFilter.setSessionManager(securityManager());
        queueKicOutFilter.setKickoutAfter(false);
        queueKicOutFilter.setKickoutUrl("/kickout");
        queueKicOutFilter.setMaxSession(1);
        return queueKicOutFilter;
    }

    /**
     *  开启shiro aop注解支持.
     *  使用代理方式;所以需要开启代码支持;
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}

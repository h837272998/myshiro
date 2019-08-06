package com.hjh.myshiro.service.impl;

import com.hjh.myshiro.entity.Permission;
import com.hjh.myshiro.service.PermissionService;
import com.hjh.myshiro.service.ShiroService;
import com.hjh.myshiro.utils.MyFilterChain;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-08-05 9:13
 */
@Service
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    PermissionService permissionService;

    @Autowired
    ShiroFilterFactoryBean shiroFilterFactoryBean;

    @Autowired
    MyFilterChain filterChain;


    /**
     * @Description:从数据库和配置文件生成FilterChain'
     * @Author: HJH
     * @Date: 2019-08-05 10:55
     * @Param: [map]
     * @Return: java.util.Map
     */
    @Override
    public Map loadFilterChainDefinitions() {
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        for (String key:filterChain.getFilterchain().keySet()){
            filterChainDefinitionMap.put(key,filterChain.getFilterchain().get(key));
        }
        List<Permission> permissions = permissionService.list();
        for (Permission permission : permissions) {
            filterChainDefinitionMap.put(permission.getUrl(), "perms[" + permission.getName() + "]");
        }
        filterChainDefinitionMap.put("/**", "user");
        return filterChainDefinitionMap;
    }


    /**
     * @Description:动态修改拦截器。实现动态权限更新
     * @Author: HJH
     * @Date: 2019-08-05 10:56
     * @Param: []
     * @Return: void
     */
    @Override
    public void updatePerission() {
        synchronized (shiroFilterFactoryBean) {
            AbstractShiroFilter shiroFilter = null;
            try {
                shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean
                        .getObject();
            } catch (Exception e) {
                throw new RuntimeException(
                        "get ShiroFilter from shiroFilterFactoryBean error!");
            }
            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                    .getFilterChainResolver();
            DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver
                    .getFilterChainManager();
            // 清空老的权限控制
            manager.getFilterChains().clear();
            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
            shiroFilterFactoryBean
                    .setFilterChainDefinitionMap(loadFilterChainDefinitions());
            // 重新构建生成
            Map<String, String> chains = shiroFilterFactoryBean
                    .getFilterChainDefinitionMap();
            for (Map.Entry<String, String> entry : chains.entrySet()) {
                String url = entry.getKey();
                String chainDefinition = entry.getValue().trim()
                        .replace(" ", "");
                manager.createChain(url, chainDefinition);
            }
            System.out.println("更新权限成功！！");
        }
    }
}

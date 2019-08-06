package com.hjh.myshiro.filter;

import com.hjh.myshiro.service.PermissionService;
import com.hjh.myshiro.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Set;

/**
 * @Description:url拦截器。权限认证.  抛弃这种方法
 * @Author: HJH
 * @Date: 2019-08-03 13:56
 */
@Slf4j
public class URLPathMatchingFilter extends PathMatchingFilter {
    @Autowired
    PermissionService permissionService;

    public URLPathMatchingFilter() {
        super();
    }

    /**
     * @Description: 访问url before
     * @Author: HJH
     * @Date: 2019-08-03 14:15
     * @Param: [request, response]
     * @Return: boolean true:通过。false 不通过
     */
    @Override
    @Cacheable
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        //由于这个过滤器不是由bean注入。即不是由context管理的需要手动获取
        if(null==permissionService){
            permissionService = SpringContextUtils.getContext().getBean(PermissionService.class);

        }

        String requestURL = getPathWithinApplication(request);
        log.info("---- HJH ---- requestURL: "+requestURL);

        Subject subject = SecurityUtils.getSubject();
        //当期映射中没有登录。进行跳转
        if (!subject.isAuthenticated()){
            WebUtils.issueRedirect(request,response,"/login");
            return false;
        }
        boolean needInterceptor = permissionService.needInterceptor(requestURL);
        //权限连接查询。如果该链接不存在权限表，放行
        if (!needInterceptor){
            return true;
        }
        //判断用户是否有该URL权限
        else {
            boolean hasPermission = false;
            String userName = subject.getPrincipal().toString();
            Set<String> permissionUrls = permissionService.listPermissionURLs(userName);
            for (String url : permissionUrls) {
                // 这就表示当前用户有这个权限
                if (url.equals(requestURL)) {
                    hasPermission = true;
                    break;
                }
            }

            if (hasPermission) {
                return true;
            } else {
                UnauthorizedException ex = new UnauthorizedException("当前用户没有访问路径 " + requestURL + " 的权限");
                subject.getSession().setAttribute("message", ex.getMessage());

                WebUtils.issueRedirect(request, response, "/unauthorized");
                return false;
            }
        }
    }
}

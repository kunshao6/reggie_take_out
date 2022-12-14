package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已完成登录
 */
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求: {}",requestURI);
        //定义不需要请求的URI
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"

        };
        //判断本次匹配是否需要处理
        boolean check = check(urls, requestURI);
        //如果不需要处理，则直接放行
        log.info("本次请求{}不需要处理",requestURI);
        if (check){
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态，如果已登录，则放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，id为: {}",request.getSession().getAttribute("employee"));
            //获取用户id，用于公共字段自动填充的操作使用
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态，如果已登录，则放行
        if (request.getSession().getAttribute("user")!=null){
            log.info("用户已登录，id为: {}",request.getSession().getAttribute("user"));
            //获取用户id，用于公共字段自动填充的操作使用
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        //如果未登录，则返回未登录结果
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 路径匹配，判断本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match == true)
                return true;

        }
        return false;
    }
}

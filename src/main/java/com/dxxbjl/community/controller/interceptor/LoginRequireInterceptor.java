package com.dxxbjl.community.controller.interceptor;

import com.dxxbjl.community.annotation.LoginRequired;
import com.dxxbjl.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 拦截带有登录注解的方法
 */
@Component
public class LoginRequireInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    /**
     *  拦截器
     * @param request
     * @param response
     * @param handler  拦截的目标
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断拦截的目标是不是方法
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            //拦截带有@LoginRequired注解的方法
            LoginRequired loginRequired =  method.getAnnotation(LoginRequired.class);
            //判断用户有没有登录，没有登录就重定向到登陆页
            if(loginRequired != null && hostHolder.getUser() == null){
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}

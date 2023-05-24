package com.dxxbjl.community.controller.interceptor;

import com.dxxbjl.community.entity.LoginTicket;
import com.dxxbjl.community.entity.User;
import com.dxxbjl.community.service.UserService;
import com.dxxbjl.community.util.CookieUtil;
import com.dxxbjl.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //拦截器
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证
        String ticket = CookieUtil.getValue(request,"ticket");
        if(ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效
            //不为空，状态为正常，超时时间在当前时间之后
            if(loginTicket !=null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有用户
                //考虑多线程,进行线程隔离  ThreadLocal
                hostHolder.setUsers(user);
            }
        }
        return true;
    }

    //拦截器
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        System.out.println("****");
        System.out.println(user);
        if(user != null && modelAndView !=null){
            modelAndView.addObject("loginUser",user);
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();//清理数据
    }
}

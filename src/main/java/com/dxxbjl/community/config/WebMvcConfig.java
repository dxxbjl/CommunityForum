package com.dxxbjl.community.config;

import com.dxxbjl.community.controller.interceptor.AlphaInterceptor;
import com.dxxbjl.community.controller.interceptor.LoginRequireInterceptor;
import com.dxxbjl.community.controller.interceptor.LoginTicketInterceptor;
import com.dxxbjl.community.controller.interceptor.MessageInterceptor;
import com.dxxbjl.community.dao.LoginTicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

//    @Autowired
//    private LoginRequireInterceptor loginRequireInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    /**
     * 拦截器配置
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //不拦截静态资源
        //只拦截注册和登录
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.jepg")
                .addPathPatterns("/register","/login");

        //拦截器,除了静态资源拦截所有的
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        //拦截器,除了静态资源拦截所有的带登陆注解@loginRequire的方法
//        registry.addInterceptor(loginRequireInterceptor)
//                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

    }

}

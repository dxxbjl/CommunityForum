package com.dxxbjl.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//登录注解，限制未登录用户使用登陆后的功能
@Target(ElementType.METHOD)//写在方法上的
@Retention(RetentionPolicy.RUNTIME)//程序运行时生效
//有这个标记才意味着 只有在登陆时才能访问
public @interface LoginRequired {

}

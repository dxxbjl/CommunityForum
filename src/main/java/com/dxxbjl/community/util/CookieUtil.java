package com.dxxbjl.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    /**
     * 查找指定的cookie
     * @param request
     * @param name
     * @return
     */
    public static String getValue(HttpServletRequest request,String name){
        if(request == null || name ==null){
            throw new IllegalArgumentException("参数为空");
        }

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        //如果没找到对应的cookie
        return null;
    }
}

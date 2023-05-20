package com.dxxbjl.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

//配置类
//@Configuration用于声明配置类，该注解是基于@Component实现的
@Configuration
public class AlphaConfig {

    @Bean
    public SimpleDateFormat simpleDateFormat(){
        //这个对象返回的对象将被装配到容器中
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}

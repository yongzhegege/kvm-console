package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.handle.MyHandle;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyHandle())
                //制定拦截的路径
                .addPathPatterns("/**")
                //添加不拦截的路径
                .excludePathPatterns("/user/login", "/source/**", "/login.html","/layuiadmin/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
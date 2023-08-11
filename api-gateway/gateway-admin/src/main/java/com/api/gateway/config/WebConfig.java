package com.api.gateway.config;

import com.api.gateway.interceptor.LoginInterceptor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private static List<String> ignoreUrlList = Lists.newArrayList("/app/register",
            "/app/unregister", "/user/login", "/user/login/page","/static/**");
    //@Resource(注入不成功，why?)
    @Autowired(required = false)
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(ignoreUrlList);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
}

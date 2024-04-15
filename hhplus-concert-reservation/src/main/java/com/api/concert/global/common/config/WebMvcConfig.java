package com.api.concert.global.common.config;

import com.api.concert.controller.interceptors.QueueStatusCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new QueueStatusCheckInterceptor())
                .addPathPatterns("/concert/reservation/dates");
    }
}

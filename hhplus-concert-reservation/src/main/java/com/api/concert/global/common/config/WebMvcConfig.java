package com.api.concert.global.common.config;

import com.api.concert.controller.interceptors.QueueStatusCheckInterceptor;
import com.api.concert.domain.queue.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final QueueService queueService;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new QueueStatusCheckInterceptor(queueService))
                .addPathPatterns(
                        "/concert/reservation/dates",
                        "/concert/{concertOptionId}/reservation/seats",
                        "/concert/reservation",
                        "/concert/payment"
                );
    }
}

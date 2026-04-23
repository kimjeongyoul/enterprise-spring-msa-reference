package com.global.auth.common.config;

import com.global.auth.common.handler.GlobalExceptionHandler;
import com.global.auth.common.filter.LoggingFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * common-lib 기능을 자동으로 활성화하는 전사 공통 자동 설정 클래스
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;

import com.global.auth.common.context.UserContextInterceptor;
import feign.RequestInterceptor;

@AutoConfiguration
@Import({I18nConfig.class, JacksonConfig.class, ResilienceConfig.class})
public class CommonAutoConfiguration {

    @Bean
    public GlobalExceptionHandler globalExceptionHandler(MessageSource messageSource) {
        return new GlobalExceptionHandler(messageSource);
    }

    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new FeignErrorDecoder(objectMapper);
    }

    @Bean
    public RequestInterceptor userContextInterceptor() {
        return new UserContextInterceptor();
    }
}

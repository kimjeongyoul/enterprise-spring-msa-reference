package com.global.auth.common.context;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * Feign 호출 시 UserContext 정보를 헤더로 자동 전파
 */
@Component
public class UserContextInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String userId = UserContext.getUserId();
        if (userId != null) {
            template.header("X-User-ID", userId);
        }
        
        String correlationId = UserContext.getCorrelationId();
        if (correlationId != null) {
            template.header("X-Correlation-ID", correlationId);
        }
    }
}

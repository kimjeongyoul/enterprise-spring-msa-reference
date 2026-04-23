package com.global.auth.common.filter;

import com.global.auth.common.context.UserContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class LoggingFilter implements Filter {
    private static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 1. Correlation ID 설정
        String correlationId = httpRequest.getHeader("X-Correlation-ID");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().substring(0, 8);
        }
        
        // 2. User ID 설정
        String userId = httpRequest.getHeader("X-User-ID");
        
        // 3. Context & MDC 저장
        UserContext.setCorrelationId(correlationId);
        UserContext.setUserId(userId);
        MDC.put(TRACE_ID, correlationId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
            UserContext.clear();
        }
    }
}

package com.global.auth.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.common.dto.ApiResponse;
import com.global.auth.gateway.exception.GatewayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 게이트웨이 전역 에러 핸들러 (Reactive)
 * 라우팅 실패, 필터 예외 등을 ApiResponse 규격으로 통합 처리
 */
@Slf4j
@Component
@Order(-1) // 최우선 처리
@RequiredArgsConstructor
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 응답 컨텐츠 타입 설정
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // 1. 상태 코드 및 에러 정보 매핑
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorCode = "GW-500";
        String message = "Gateway Internal Error";

        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason();
        }
        
        log.error("[Gateway Error] Path: {}, Message: {}", exchange.getRequest().getPath(), ex.getMessage());

        // 2. ApiResponse 규격 생성
        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode, message);
        response.setStatusCode(status);

        // 3. JSON 직렬화 및 출력
        try {
            byte[] bytes = objectMapper.writeValueAsString(apiResponse).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error writing response", e);
            return Mono.error(e);
        }
    }
}

package com.global.auth.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.common.dto.ApiResponse;
import com.global.auth.gateway.exception.GatewayErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;
    private SecretKey key;
    private final List<String> whiteList = List.of("/api/v1/auth/signup", "/api/v1/auth/login");

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. Correlation ID 설정 (트레이싱 시작)
        String correlationId = request.getHeaders().getFirst("X-Correlation-ID");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().substring(0, 8);
        }

        // 2. White List 체크
        if (whiteList.contains(path)) {
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-Correlation-ID", correlationId)
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        // 3. 인증 체크
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, GatewayErrorCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 4. 하위 서비스로 전파할 헤더 주입 (Mature MSA 규격)
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-Correlation-ID", correlationId)
                    .header("X-User-ID", claims.getSubject())
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return onError(exchange, GatewayErrorCode.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, GatewayErrorCode errorCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.valueOf(errorCode.getStatus()));
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode, errorCode.getMessageKey());
        
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

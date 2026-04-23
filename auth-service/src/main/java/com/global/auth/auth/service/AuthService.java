package com.global.auth.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.auth.domain.OutboxEvent;
import com.global.auth.auth.domain.User;
import com.global.auth.auth.dto.LoginRequest;
import com.global.auth.auth.dto.SignupRequest;
import com.global.auth.auth.dto.TokenResponse;
import com.global.auth.auth.repository.OutboxRepository;
import com.global.auth.auth.repository.UserRepository;
import com.global.auth.auth.util.JwtTokenProvider;
import com.global.auth.common.event.NotificationEvent;
import com.global.auth.common.exception.CustomException;
import com.global.auth.auth.exception.AuthErrorCode;
import com.global.auth.common.exception.CommonErrorCode;
import com.global.auth.auth.client.AuthCommonCodeClient;
import com.global.auth.common.dto.CommonCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthCommonCodeClient commonCodeClient;

    @Transactional
    @SneakyThrows
    public void signup(SignupRequest request) {
        // [Example] common-service에서 동적으로 사용자 상태 코드를 조회
        List<CommonCodeResponse> userStatuses = commonCodeClient.getCodesByGroup("USER_STATUS");
        log.info("Available User Statuses from Common Service: {}", userStatuses);
        
        // 실제 운영 환경에서는 'ACTIVE' 같은 코드의 활성화 여부를 여기서 체크 가능
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(AuthErrorCode.DUPLICATE_USER);
        }

        // 1. 비즈니스 데이터 저장
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        // 2. 아웃박스에 이벤트 저장 (동일 트랜잭션)
        NotificationEvent notification = NotificationEvent.builder()
                .receiver(request.getEmail())
                .title("Welcome!")
                .type("EMAIL")
                .build();

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType("USER")
                .eventType("SIGNUP_SUCCESS")
                .payload(objectMapper.writeValueAsString(notification))
                .build();

        outboxRepository.save(outboxEvent);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }

        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole());

        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }
}

package com.global.auth.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.auth.domain.OutboxEvent;
import com.global.auth.auth.domain.User;
import com.global.auth.auth.repository.OutboxRepository;
import com.global.auth.auth.repository.UserRepository;
import com.global.auth.common.event.NotificationEvent;
import com.global.auth.common.exception.CustomException;
import com.global.auth.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    public void signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
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
                .content("User " + request.getUsername() + " signed up.")
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole());

        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }
}

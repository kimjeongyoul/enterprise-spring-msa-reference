package com.global.auth.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.common.dto.ApiResponse;
import com.global.auth.common.exception.CustomException;
import com.global.auth.common.exception.ErrorCode;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * 원격 서비스에서 발생한 에러를 로컬 CustomException으로 복원하는 디코더
 */
@Slf4j
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            // 원격 서비스의 ApiResponse 바디를 읽음
            ApiResponse<?> apiResponse = objectMapper.readValue(response.body().asInputStream(), ApiResponse.class);
            String errorCodeStr = apiResponse.getError().getCode();
            String errorMessage = apiResponse.getError().getMessage();
            
            log.error("Feign Error [{}]: {} - code: {}", methodKey, response.status(), errorCodeStr);

            // 규격화된 에러라면 CustomException으로 복원
            return new CustomException(new ErrorCode() {
                @Override public String getCode() { return errorCodeStr; }
                @Override public String getMessageKey() { return errorMessage; }
                @Override public int getStatus() { return response.status(); }
            });

        } catch (IOException | RuntimeException e) {
            // 파싱 실패 시 기본 에러 처리
            return new Exception("Failed to decode remote error: " + response.status());
        }
    }
}

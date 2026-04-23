package com.global.auth.common.handler;

import com.global.auth.common.dto.ApiResponse;
import com.global.auth.common.exception.CustomException;
import com.global.auth.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        
        // 다국어 메시지 조회 (없으면 기본 메시지 사용)
        String message = messageSource.getMessage(
                errorCode.name(), 
                null, 
                errorCode.getMessage(), 
                LocaleContextHolder.getLocale()
        );
        
        log.warn("CustomException [{}]: {}", errorCode.name(), message);
        
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode.name(), message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().isEmpty() ? 
            "Invalid input" : bindingResult.getFieldErrors().get(0).getDefaultMessage();
            
        log.warn("Validation Exception: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_INPUT.name(), errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Internal Server Error: ", ex);
        
        String message = messageSource.getMessage(
                ErrorCode.COMMON_ERROR.name(), 
                null, 
                ErrorCode.COMMON_ERROR.getMessage(), 
                LocaleContextHolder.getLocale()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.COMMON_ERROR.name(), message));
    }
}

package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.exception.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // (1) 비즈니스 로직 중 발생하는 런타임 예외 처리
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
                log.error("Business RuntimeException: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ErrorResponse.builder()
                                                .status(HttpStatus.BAD_REQUEST.value())
                                                .code("BUSINESS_ERROR")
                                                .message(e.getMessage())
                                                .build());
        }

        // (2) Security - 401 Unauthorized 처리 (AuthenticationEntryPoint에서 던짐)
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
                log.error("AuthenticationException (401): {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ErrorResponse.builder()
                                                .status(HttpStatus.UNAUTHORIZED.value())
                                                .code("UNAUTHORIZED")
                                                .message("인증이 필요한 서비스입니다. (토큰 만료 혹은 없음)")
                                                .build());
        }

        // (3) Security - 403 Forbidden 처리 (AccessDeniedHandler에서 던짐)
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
                log.error("AccessDeniedException (403): {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ErrorResponse.builder()
                                                .status(HttpStatus.FORBIDDEN.value())
                                                .code("FORBIDDEN")
                                                .message("해당 리소스에 접근할 권한이 없습니다.")
                                                .build());
        }

        // (4) 그 외 정의되지 않은 모든 예외
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
                log.error("Unhandled Exception: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ErrorResponse.builder()
                                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                                .code("INTERNAL_SERVER_ERROR")
                                                .message("서버 내부 오류가 발생했습니다.")
                                                .build());
        }
}

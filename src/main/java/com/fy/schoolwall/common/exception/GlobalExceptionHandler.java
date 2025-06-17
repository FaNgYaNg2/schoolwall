package com.fy.schoolwall.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局异常处理器
 * 
 * 意义：
 * 1. 统一处理应用程序中的异常，避免在每个Controller中重复异常处理代码
 * 2. 提供一致的错误响应格式，便于前端统一处理
 * 3. 将技术异常转换为用户友好的错误信息
 * 4. 记录异常日志，便于问题排查
 * 5. 提高代码可维护性和用户体验
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        System.err.println("Resource not found: " + ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * 处理参数验证异常（@Valid注解触发）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        System.err.println("Validation error: " + ex.getMessage());

        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> fieldErrors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_FAILED",
                "请求参数验证失败",
                LocalDateTime.now(),
                fieldErrors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        System.err.println("Constraint violation: " + ex.getMessage());

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        Map<String, String> fieldErrors = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            fieldErrors.put(propertyPath, message);
        }

        ErrorResponse error = new ErrorResponse(
                "CONSTRAINT_VIOLATION",
                "数据约束违反",
                LocalDateTime.now(),
                fieldErrors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        System.err.println("Authentication error: " + ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "AUTHENTICATION_FAILED",
                "认证失败，请检查用户名和密码",
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 处理凭证错误异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        System.err.println("Bad credentials: " + ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "BAD_CREDENTIALS",
                "用户名或密码错误",
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 处理访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        System.err.println("Access denied: " + ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "ACCESS_DENIED",
                "权限不足，无法访问该资源",
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * 处理业务异常（RuntimeException）
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        System.err.println("Runtime exception: " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                "BUSINESS_ERROR",
                ex.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理其他未预期的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        System.err.println("Unexpected error: " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "系统内部错误，请稍后重试",
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 错误响应格式
     */
    public static class ErrorResponse {
        private String code;
        private String message;
        private LocalDateTime timestamp;
        private Map<String, String> fieldErrors;

        public ErrorResponse(String code, String message, LocalDateTime timestamp) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
        }

        public ErrorResponse(String code, String message, LocalDateTime timestamp, Map<String, String> fieldErrors) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
            this.fieldErrors = fieldErrors;
        }

        // Getters and Setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public Map<String, String> getFieldErrors() {
            return fieldErrors;
        }

        public void setFieldErrors(Map<String, String> fieldErrors) {
            this.fieldErrors = fieldErrors;
        }
    }
}
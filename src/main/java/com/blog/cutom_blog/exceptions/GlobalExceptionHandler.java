package com.blog.cutom_blog.exceptions;

import com.blog.cutom_blog.dtos.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle WebApiException and its subclasses (ConflictException, NotFoundException, ForbiddenException, etc.)
     */
    @ExceptionHandler(WebApiException.class)
    public ResponseEntity<ApiResponse<?>> handleWebApiException(WebApiException ex) {
        log.error("WebApiException occurred: {}", ex.getMessage(), ex);

        // Use prettyMessage if available, otherwise use the exception message
        String message = ex.getPrettyMessage() != null ? ex.getPrettyMessage() : ex.getMessage();

        ApiResponse<?> response = ApiResponse.builder()
            .message(message)
            .data(null)
            .build();

        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(response);
    }

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error occurred: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponse<?> response = ApiResponse.builder()
            .message("Validation failed")
            .data(errors)
            .build();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    /**
     * Handle Spring Security BadCredentialsException (wrong username/password)
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(org.springframework.security.authentication.BadCredentialsException ex) {
        log.error("Bad credentials exception: {}", ex.getMessage());

        ApiResponse<?> response = ApiResponse.builder()
            .message("Incorrect email or password. Please check your credentials and try again.")
            .data(null)
            .build();

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
    }

    /**
     * Handle Spring Security AuthenticationException (general authentication errors)
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex) {
        log.error("Authentication exception: {}", ex.getMessage());

        ApiResponse<?> response = ApiResponse.builder()
            .message("Authentication failed. Please check your credentials and try again.")
            .data(null)
            .build();

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
    }

    /**
     * Handle Spring Security AccessDeniedException (authorization errors)
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        log.error("Access denied exception: {}", ex.getMessage());

        ApiResponse<?> response = ApiResponse.builder()
            .message("You do not have permission to access this resource.")
            .data(null)
            .build();

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(response);
    }

    /**
     * Handle JPA/Hibernate ConstraintViolationException (entity validation errors)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation exception: {}", ex.getMessage());

        // Extract constraint violations into a readable format
        String violations = ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));

        ApiResponse<?> response = ApiResponse.builder()
            .message("Validation error: " + violations)
            .data(null)
            .build();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

        ApiResponse<?> response = ApiResponse.builder()
            .message("An unexpected error occurred. Please try again later.")
            .data(null)
            .build();

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}
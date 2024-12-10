package org.onewayticket.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("rejectedValue", error.getRejectedValue());
            errorDetails.put("message", error.getDefaultMessage());
            fieldErrors.put(error.getField(), errorDetails);
        });
        return buildErrorResponse("Validation failed", HttpStatus.BAD_REQUEST, fieldErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, String>> errors = ex.getConstraintViolations().stream().map(violation -> {
            Map<String, String> errorDetails = new HashMap<>();
            errorDetails.put("field", violation.getPropertyPath().toString());
            errorDetails.put("message", violation.getMessage());
            return errorDetails;
        }).toList();
        return buildErrorResponse("Constraint violation", HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNoSuchElementException(NoSuchElementException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<Map<String, Object>> handleJsonMappingException(JsonMappingException ex) {
        List<Map<String, String>> errors = ex.getPath().stream().map(reference -> {
            Map<String, String> errorDetails = new HashMap<>();
            errorDetails.put("field", reference.getFieldName());
            errorDetails.put("message", "Invalid value for field");
            return errorDetails;
        }).toList();
        return buildErrorResponse("Invalid JSON format", HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleCustomDatabaseException(DataAccessException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        return buildErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }


    // 공통 오류 응답 생성 메서드
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status, Object details) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("details", details);
        return ResponseEntity.status(status).body(errorResponse);
    }
}

package com.healthwealth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(
            ResourceNotFoundException ex,
            HttpServletRequest req) {

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                req.getRequestURI()
        );
    }

    @ExceptionHandler({
            InvalidInputException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<Map<String, Object>> badRequest(
            Exception ex,
            HttpServletRequest req) {

        String message = ex.getMessage();

        if (ex instanceof MethodArgumentNotValidException e) {
            message = e.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(x -> x.getField() + ": " + x.getDefaultMessage())
                    .findFirst()
                    .orElse("Validation failed");
        }

        return build(
                HttpStatus.BAD_REQUEST,
                message,
                req.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> error(
            Exception ex,
            HttpServletRequest req) {

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                req.getRequestURI()
        );
    }

    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status,
            String message,
            String path) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);

        return ResponseEntity
                .status(status)
                .body(body);
    }
}
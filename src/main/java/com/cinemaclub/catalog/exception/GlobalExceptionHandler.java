package com.cinemaclub.catalog.exception;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiErrorResponse> notFound(ResourceNotFoundException ex, HttpServletRequest req) { return response(HttpStatus.NOT_FOUND, ex.getMessage(), req); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).collect(Collectors.joining(", "));
        return response(HttpStatus.BAD_REQUEST, message, req);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiErrorResponse> badRequest(IllegalArgumentException ex, HttpServletRequest req) { return response(HttpStatus.BAD_REQUEST, ex.getMessage(), req); }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiErrorResponse> typeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) { return response(HttpStatus.BAD_REQUEST, "Invalid value for " + ex.getName(), req); }
    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ApiErrorResponse> unavailable(IllegalStateException ex, HttpServletRequest req) { return response(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req); }
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiErrorResponse> constraintViolation(ConstraintViolationException ex, HttpServletRequest req) { return response(HttpStatus.BAD_REQUEST, ex.getMessage(), req); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception ex, HttpServletRequest req) { return response(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", req); }
    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(Instant.now(), status.value(), message, req.getRequestURI()));
    }
}

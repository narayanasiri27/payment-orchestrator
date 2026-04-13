package com.example.paymentorchestrator.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(new ApiErrorResponse(Instant.now(), status.value(), status.name(), message));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    StringBuilder sb = new StringBuilder("Validation failed");
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      sb.append("; ").append(fe.getField()).append(": ").append(fe.getDefaultMessage());
    }
    return build(HttpStatus.BAD_REQUEST, sb.toString());
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException ex) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(PaymentNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(PaymentNotFoundException ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }
}


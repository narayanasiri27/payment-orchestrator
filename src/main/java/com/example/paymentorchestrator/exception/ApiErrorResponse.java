package com.example.paymentorchestrator.exception;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
  private Instant timestamp;
  private int status;
  private String error;
  private String message;
}


package com.example.paymentorchestrator.dto;

import com.example.paymentorchestrator.enums.PaymentMethod;
import com.example.paymentorchestrator.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;

@Data
public class PaymentResponse {
  private String id;
  private BigDecimal amount;
  private String currency;
  private PaymentMethod method;
  private PaymentStatus status;
  private String primaryProvider;
  private String finalProvider;
  private String failureReason;
  private Instant createdAt;
  private Instant updatedAt;
}


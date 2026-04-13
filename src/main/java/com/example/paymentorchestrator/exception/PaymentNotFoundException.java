package com.example.paymentorchestrator.exception;

public class PaymentNotFoundException extends RuntimeException {
  public PaymentNotFoundException(String paymentId) {
    super("Payment not found: " + paymentId);
  }
}


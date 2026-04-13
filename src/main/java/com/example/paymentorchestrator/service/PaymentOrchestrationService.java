package com.example.paymentorchestrator.service;

import com.example.paymentorchestrator.dto.CreatePaymentRequest;
import com.example.paymentorchestrator.dto.PaymentResponse;

public interface PaymentOrchestrationService {
  PaymentResponse createPayment(CreatePaymentRequest request);

  PaymentResponse fetchPayment(String paymentId);
}


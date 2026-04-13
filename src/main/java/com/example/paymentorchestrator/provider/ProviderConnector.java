package com.example.paymentorchestrator.provider;

import com.example.paymentorchestrator.dto.CreatePaymentRequest;
import com.example.paymentorchestrator.entity.PaymentEntity;
import com.example.paymentorchestrator.enums.PaymentProvider;

public interface ProviderConnector {
  PaymentProvider provider();

  ProviderChargeResult charge(PaymentEntity payment, CreatePaymentRequest request);
}


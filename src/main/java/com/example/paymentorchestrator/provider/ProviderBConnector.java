package com.example.paymentorchestrator.provider;

import com.example.paymentorchestrator.dto.CreatePaymentRequest;
import com.example.paymentorchestrator.entity.PaymentEntity;
import com.example.paymentorchestrator.enums.PaymentProvider;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProviderBConnector implements ProviderConnector {
  private static final BigDecimal ONE = new BigDecimal("1");
  private static final BigDecimal FAIL_FRACTION = new BigDecimal("0.17");

  @Override
  public PaymentProvider provider() {
    return PaymentProvider.PROVIDER_B;
  }

  @Override
  public ProviderChargeResult charge(PaymentEntity payment, CreatePaymentRequest request) {
    // Simulated provider behavior: in real world this would be an HTTP call.
    // Deterministic failure knob (useful for demos/tests): amount fractional part equals 0.17 fails retriable.
    if (request.getAmount() != null
        && request.getAmount().remainder(ONE).abs().compareTo(FAIL_FRACTION) == 0) {
      return ProviderChargeResult.failure("Provider B simulated retriable failure", true);
    }
    return ProviderChargeResult.success("B-" + UUID.randomUUID());
  }
}


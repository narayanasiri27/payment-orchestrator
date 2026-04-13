package com.example.paymentorchestrator.routing;

import com.example.paymentorchestrator.enums.PaymentMethod;
import com.example.paymentorchestrator.enums.PaymentProvider;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RoutingEngine {
  public List<PaymentProvider> route(PaymentMethod method) {
    if (method == PaymentMethod.CARD) {
      return Arrays.asList(PaymentProvider.PROVIDER_A, PaymentProvider.PROVIDER_B);
    }
    if (method == PaymentMethod.UPI) {
      return Arrays.asList(PaymentProvider.PROVIDER_B, PaymentProvider.PROVIDER_A);
    }
    throw new IllegalArgumentException("Unsupported method: " + method);
  }
}


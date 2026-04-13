package com.example.paymentorchestrator.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.paymentorchestrator.enums.PaymentMethod;
import com.example.paymentorchestrator.enums.PaymentProvider;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoutingEngineTest {
  private final RoutingEngine routingEngine = new RoutingEngine();

  @Test
  void cardRoutesToProviderAThenB() {
    List<PaymentProvider> route = routingEngine.route(PaymentMethod.CARD);
    assertEquals(Arrays.asList(PaymentProvider.PROVIDER_A, PaymentProvider.PROVIDER_B), route);
  }

  @Test
  void upiRoutesToProviderBThenA() {
    List<PaymentProvider> route = routingEngine.route(PaymentMethod.UPI);
    assertEquals(Arrays.asList(PaymentProvider.PROVIDER_B, PaymentProvider.PROVIDER_A), route);
  }
}


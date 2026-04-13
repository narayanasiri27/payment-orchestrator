package com.example.paymentorchestrator.routing;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RoutingEngineAdditionalTest {
  private final RoutingEngine routingEngine = new RoutingEngine();

  @Test
  void nullMethodThrowsIllegalArgument() {
    assertThrows(IllegalArgumentException.class, () -> routingEngine.route(null));
  }
}


package com.example.paymentorchestrator;

import org.junit.jupiter.api.Test;

class PaymentOrchestratorApplicationTest {
  @Test
  void mainDoesNotThrow() {
    PaymentOrchestratorApplication.main(new String[] {"--spring.main.web-application-type=none"});
  }
}


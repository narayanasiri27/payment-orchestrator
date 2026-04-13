package com.example.paymentorchestrator.provider;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProviderChargeResultTest {

  @Test
  void successFactorySetsFields() {
    ProviderChargeResult r = ProviderChargeResult.success("ref-1");
    assertTrue(r.isSuccess());
    assertFalse(r.isRetriable());
    assertNull(r.getFailureReason());
    assertTrue(r.getProviderReference().contains("ref-1"));
  }

  @Test
  void failureFactorySetsFields() {
    ProviderChargeResult r = ProviderChargeResult.failure("boom", true);
    assertFalse(r.isSuccess());
    assertTrue(r.isRetriable());
    assertNull(r.getProviderReference());
    assertTrue(r.getFailureReason().contains("boom"));
  }
}


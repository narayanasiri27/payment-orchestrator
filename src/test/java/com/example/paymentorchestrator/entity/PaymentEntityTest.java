package com.example.paymentorchestrator.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.example.paymentorchestrator.enums.PaymentMethod;
import com.example.paymentorchestrator.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PaymentEntityTest {

  @Test
  void ensureIdGeneratesWhenMissing() {
    PaymentEntity e = new PaymentEntity();
    e.ensureId();
    assertNotNull(e.getPaymentId());
  }

  @Test
  void ensureIdDoesNotOverrideExisting() {
    PaymentEntity e = new PaymentEntity();
    e.setPaymentId("existing");
    e.ensureId();
    assertSame("existing", e.getPaymentId());
  }

  @Test
  void lombokGettersSettersWork() {
    PaymentEntity e = new PaymentEntity();
    e.setAmount(new BigDecimal("1.00"));
    e.setCurrency("INR");
    e.setMethod(PaymentMethod.UPI);
    e.setStatus(PaymentStatus.PROCESSING);
    e.setCreatedAt(Instant.now());
    e.setUpdatedAt(Instant.now());
    assertNotNull(e.getAmount());
    assertNotNull(e.getCurrency());
    assertNotNull(e.getMethod());
    assertNotNull(e.getStatus());
    assertNotNull(e.getCreatedAt());
    assertNotNull(e.getUpdatedAt());
  }
}


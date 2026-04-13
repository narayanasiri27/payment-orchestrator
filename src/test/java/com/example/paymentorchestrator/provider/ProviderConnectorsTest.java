package com.example.paymentorchestrator.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.paymentorchestrator.dto.CreatePaymentRequest;
import com.example.paymentorchestrator.entity.PaymentEntity;
import com.example.paymentorchestrator.enums.PaymentMethod;
import com.example.paymentorchestrator.enums.PaymentProvider;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProviderConnectorsTest {

  @Test
  void providerAReportsCorrectProviderEnum() {
    assertEquals(PaymentProvider.PROVIDER_A, new ProviderAConnector().provider());
  }

  @Test
  void providerBReportsCorrectProviderEnum() {
    assertEquals(PaymentProvider.PROVIDER_B, new ProviderBConnector().provider());
  }

  @Test
  void providerAFailsRetriablyForFraction013() {
    CreatePaymentRequest req = new CreatePaymentRequest();
    req.setAmount(new BigDecimal("10.13"));
    req.setCurrency("INR");
    req.setMethod(PaymentMethod.CARD);

    ProviderChargeResult r = new ProviderAConnector().charge(new PaymentEntity(), req);
    assertFalse(r.isSuccess());
    assertTrue(r.isRetriable());
  }

  @Test
  void providerASucceedsOtherwise() {
    CreatePaymentRequest req = new CreatePaymentRequest();
    req.setAmount(new BigDecimal("10.12"));
    req.setCurrency("INR");
    req.setMethod(PaymentMethod.CARD);

    ProviderChargeResult r = new ProviderAConnector().charge(new PaymentEntity(), req);
    assertTrue(r.isSuccess());
  }

  @Test
  void providerBFailsRetriablyForFraction017() {
    CreatePaymentRequest req = new CreatePaymentRequest();
    req.setAmount(new BigDecimal("10.17"));
    req.setCurrency("INR");
    req.setMethod(PaymentMethod.UPI);

    ProviderChargeResult r = new ProviderBConnector().charge(new PaymentEntity(), req);
    assertFalse(r.isSuccess());
    assertTrue(r.isRetriable());
  }

  @Test
  void providerBSucceedsOtherwise() {
    CreatePaymentRequest req = new CreatePaymentRequest();
    req.setAmount(new BigDecimal("10.16"));
    req.setCurrency("INR");
    req.setMethod(PaymentMethod.UPI);

    ProviderChargeResult r = new ProviderBConnector().charge(new PaymentEntity(), req);
    assertTrue(r.isSuccess());
  }
}


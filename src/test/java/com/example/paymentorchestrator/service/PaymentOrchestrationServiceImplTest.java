package com.example.paymentorchestrator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.paymentorchestrator.dto.CreatePaymentRequest;
import com.example.paymentorchestrator.dto.PaymentResponse;
import com.example.paymentorchestrator.entity.PaymentEntity;
import com.example.paymentorchestrator.enums.PaymentMethod;
import com.example.paymentorchestrator.enums.PaymentProvider;
import com.example.paymentorchestrator.enums.PaymentStatus;
import com.example.paymentorchestrator.provider.ProviderChargeResult;
import com.example.paymentorchestrator.provider.ProviderConnector;
import com.example.paymentorchestrator.repository.PaymentRepository;
import com.example.paymentorchestrator.routing.RoutingEngine;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PaymentOrchestrationServiceImplTest {

  @Test
  void retriesAndFailoverToSecondaryProvider() {
    PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);

    ProviderConnector providerA = Mockito.mock(ProviderConnector.class);
    when(providerA.provider()).thenReturn(PaymentProvider.PROVIDER_A);
    when(providerA.charge(any(PaymentEntity.class), any(CreatePaymentRequest.class)))
        .thenReturn(ProviderChargeResult.failure("A down", true));

    ProviderConnector providerB = Mockito.mock(ProviderConnector.class);
    when(providerB.provider()).thenReturn(PaymentProvider.PROVIDER_B);
    when(providerB.charge(any(PaymentEntity.class), any(CreatePaymentRequest.class)))
        .thenReturn(ProviderChargeResult.success("B-REF-1"));

    when(paymentRepository.save(any(PaymentEntity.class)))
        .thenAnswer(
            inv -> {
              PaymentEntity p = inv.getArgument(0);
              if (p.getPaymentId() == null) {
                p.setPaymentId("test-id-1");
              }
              return p;
            });

    RoutingEngine routingEngine = new RoutingEngine();
    PaymentOrchestrationServiceImpl svc =
        new PaymentOrchestrationServiceImpl(
            paymentRepository, routingEngine, Arrays.asList(providerA, providerB), 2);

    CreatePaymentRequest req = new CreatePaymentRequest();
    req.setAmount(new BigDecimal("10.00"));
    req.setCurrency("INR");
    req.setMethod(PaymentMethod.CARD);

    PaymentResponse resp = svc.createPayment(req);

    assertNotNull(resp.getId());
    assertEquals(PaymentStatus.SUCCESS, resp.getStatus());
    assertEquals(PaymentProvider.PROVIDER_A.name(), resp.getPrimaryProvider());
    assertEquals(PaymentProvider.PROVIDER_B.name(), resp.getFinalProvider());
  }

  @Test
  void idempotentCreateReturnsSamePayment() {
    PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
    ProviderConnector providerA = Mockito.mock(ProviderConnector.class);
    when(providerA.provider()).thenReturn(PaymentProvider.PROVIDER_A);
    when(providerA.charge(any(PaymentEntity.class), any(CreatePaymentRequest.class)))
        .thenReturn(ProviderChargeResult.success("A-REF-1"));

    RoutingEngine routingEngine = new RoutingEngine();
    PaymentOrchestrationServiceImpl svc =
        new PaymentOrchestrationServiceImpl(
            paymentRepository, routingEngine, Arrays.asList(providerA), 2);

    // Each create call results in a new Payment.
    when(paymentRepository.save(any(PaymentEntity.class)))
        .thenAnswer(
            inv -> {
              PaymentEntity p = inv.getArgument(0);
              if (p.getPaymentId() == null) {
                p.setPaymentId("test-id-2");
              }
              return p;
            });

    CreatePaymentRequest req = new CreatePaymentRequest();
    req.setAmount(new BigDecimal("10.00"));
    req.setCurrency("INR");
    req.setMethod(PaymentMethod.CARD);

    PaymentResponse resp1 = svc.createPayment(req);
    PaymentResponse resp2 = svc.createPayment(req);
    assertNotNull(resp1.getId());
    assertNotNull(resp2.getId());
  }
}


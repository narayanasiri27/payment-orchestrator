package com.example.paymentorchestrator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.paymentorchestrator.dto.CreatePaymentRequest;
import com.example.paymentorchestrator.dto.PaymentResponse;
import com.example.paymentorchestrator.entity.PaymentEntity;
import com.example.paymentorchestrator.enums.PaymentMethod;
import com.example.paymentorchestrator.enums.PaymentProvider;
import com.example.paymentorchestrator.enums.PaymentStatus;
import com.example.paymentorchestrator.exception.PaymentNotFoundException;
import com.example.paymentorchestrator.provider.ProviderChargeResult;
import com.example.paymentorchestrator.provider.ProviderConnector;
import com.example.paymentorchestrator.repository.PaymentRepository;
import com.example.paymentorchestrator.routing.RoutingEngine;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PaymentOrchestrationServiceImplAdditionalTest {

  @Test
  void fetchPaymentNotFoundThrows() {
    PaymentRepository repo = Mockito.mock(PaymentRepository.class);
    when(repo.findById("missing")).thenReturn(Optional.empty());

    PaymentOrchestrationServiceImpl svc =
        new PaymentOrchestrationServiceImpl(repo, new RoutingEngine(), Arrays.<ProviderConnector>asList(), 1);

    assertThrows(PaymentNotFoundException.class, () -> svc.fetchPayment("missing"));
  }

  @Test
  void createPaymentFailsWhenAllProvidersFail() {
    PaymentRepository repo = Mockito.mock(PaymentRepository.class);

    ProviderConnector a = Mockito.mock(ProviderConnector.class);
    when(a.provider()).thenReturn(PaymentProvider.PROVIDER_A);
    when(a.charge(any(PaymentEntity.class), any(CreatePaymentRequest.class)))
        .thenReturn(ProviderChargeResult.failure("nope", false));

    ProviderConnector b = Mockito.mock(ProviderConnector.class);
    when(b.provider()).thenReturn(PaymentProvider.PROVIDER_B);
    when(b.charge(any(PaymentEntity.class), any(CreatePaymentRequest.class)))
        .thenReturn(ProviderChargeResult.failure("nope", false));

    when(repo.save(any(PaymentEntity.class)))
        .thenAnswer(
            inv -> {
              PaymentEntity p = inv.getArgument(0);
              if (p.getPaymentId() == null) {
                p.setPaymentId("id-1");
              }
              return p;
            });

    PaymentOrchestrationServiceImpl svc =
        new PaymentOrchestrationServiceImpl(repo, new RoutingEngine(), Arrays.asList(a, b), 1);

    CreatePaymentRequest req = new CreatePaymentRequest();
    req.setAmount(new BigDecimal("1.00"));
    req.setCurrency("INR");
    req.setMethod(PaymentMethod.CARD);

    PaymentResponse resp = svc.createPayment(req);
    assertNotNull(resp.getId());
    assertEquals(PaymentStatus.FAILED, resp.getStatus());
    assertEquals(PaymentProvider.PROVIDER_A.name(), resp.getPrimaryProvider());
    assertEquals(PaymentProvider.PROVIDER_B.name(), resp.getFinalProvider());
  }
}


package com.example.paymentorchestrator.service;

import com.example.paymentorchestrator.dto.CreatePaymentRequest;
import com.example.paymentorchestrator.dto.PaymentResponse;
import com.example.paymentorchestrator.entity.PaymentEntity;
import com.example.paymentorchestrator.enums.PaymentProvider;
import com.example.paymentorchestrator.enums.PaymentStatus;
import com.example.paymentorchestrator.exception.PaymentNotFoundException;
import com.example.paymentorchestrator.provider.ProviderChargeResult;
import com.example.paymentorchestrator.provider.ProviderConnector;
import com.example.paymentorchestrator.repository.PaymentRepository;
import com.example.paymentorchestrator.routing.RoutingEngine;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentOrchestrationServiceImpl implements PaymentOrchestrationService {
  private final PaymentRepository paymentRepository;
  private final RoutingEngine routingEngine;
  private final Map<PaymentProvider, ProviderConnector> connectorsByProvider;
  private final int maxAttemptsPerProvider;

  public PaymentOrchestrationServiceImpl(
      PaymentRepository paymentRepository,
      RoutingEngine routingEngine,
      List<ProviderConnector> connectors,
      @Value("${app.orchestration.maxAttemptsPerProvider:2}") int maxAttemptsPerProvider) {
    this.paymentRepository = paymentRepository;
    this.routingEngine = routingEngine;
    this.connectorsByProvider =
        connectors.stream().collect(Collectors.toMap(ProviderConnector::provider, c -> c));
    this.maxAttemptsPerProvider = maxAttemptsPerProvider;
  }

  @Override
  @Transactional
  public PaymentResponse createPayment(CreatePaymentRequest request) {
    List<PaymentProvider> route = routingEngine.route(request.getMethod());

    PaymentEntity payment = new PaymentEntity();
    payment.setAmount(request.getAmount());
    payment.setCurrency(request.getCurrency().toUpperCase());
    payment.setMethod(request.getMethod());
    payment.setStatus(PaymentStatus.PROCESSING);
    if (!route.isEmpty()) {
      payment.setPrimaryProvider(route.get(0));
    }
    payment.setCreatedAt(Instant.now());
    payment.setUpdatedAt(Instant.now());
    paymentRepository.save(payment);

    PaymentEntity processed = processWithRouting(payment, request);
    return toResponse(processed);
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentResponse fetchPayment(String paymentId) {
    PaymentEntity payment =
        paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    return toResponse(payment);
  }

  private PaymentEntity processWithRouting(PaymentEntity payment, CreatePaymentRequest request) {
    List<PaymentProvider> route = routingEngine.route(payment.getMethod());

    for (PaymentProvider provider : route) {
      ProviderConnector connector = connectorsByProvider.get(provider);
      if (connector == null) {
        continue;
      }

      for (int attempt = 1; attempt <= maxAttemptsPerProvider; attempt++) {
        ProviderChargeResult result = connector.charge(payment, request);
        if (result == null) {
          payment.setFailureReason("Provider " + provider + " returned empty result");
          payment.setFinalProvider(provider);
          payment.setUpdatedAt(Instant.now());
          paymentRepository.save(payment);
          break;
        }
        if (result.isSuccess()) {
          payment.setProvider(provider);
          payment.setFinalProvider(provider);
          payment.setProviderReference(result.getProviderReference());
          payment.setStatus(PaymentStatus.SUCCESS);
          payment.setFailureReason(null);
          payment.setUpdatedAt(Instant.now());
          return paymentRepository.save(payment);
        }

        if (!result.isRetriable()) {
          break;
        }
      }

      payment.setFailureReason("Provider " + provider + " failed after retries");
      payment.setFinalProvider(provider);
      payment.setUpdatedAt(Instant.now());
      paymentRepository.save(payment);
    }

    payment.setStatus(PaymentStatus.FAILED);
    payment.setUpdatedAt(Instant.now());
    payment.setFailureReason(
        payment.getFailureReason() != null ? payment.getFailureReason() : "All providers failed");
    return paymentRepository.save(payment);
  }

  private PaymentResponse toResponse(PaymentEntity p) {
    PaymentResponse r = new PaymentResponse();
    r.setId(p.getPaymentId());
    r.setAmount(p.getAmount());
    r.setCurrency(p.getCurrency());
    r.setMethod(p.getMethod());
    r.setStatus(p.getStatus());
    r.setPrimaryProvider(p.getPrimaryProvider() != null ? p.getPrimaryProvider().name() : null);
    r.setFinalProvider(p.getFinalProvider() != null ? p.getFinalProvider().name() : null);
    r.setFailureReason(p.getFailureReason());
    r.setCreatedAt(p.getCreatedAt());
    r.setUpdatedAt(p.getUpdatedAt());
    return r;
  }
}


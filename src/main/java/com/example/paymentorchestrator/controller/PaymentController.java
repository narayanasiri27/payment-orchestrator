package com.example.paymentorchestrator.controller;

import com.example.paymentorchestrator.dto.CreatePaymentRequest;
import com.example.paymentorchestrator.dto.PaymentResponse;
import com.example.paymentorchestrator.service.PaymentOrchestrationService;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController {
  private final PaymentOrchestrationService service;

  public PaymentController(PaymentOrchestrationService service) {
    this.service = service;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public PaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
    return service.createPayment(request);
  }

  @GetMapping("/{paymentId}")
  public PaymentResponse fetchPayment(@PathVariable("paymentId") String paymentId) {
    return service.fetchPayment(paymentId);
  }
}


package com.example.paymentorchestrator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.paymentorchestrator.dto.PaymentResponse;
import com.example.paymentorchestrator.enums.PaymentMethod;
import com.example.paymentorchestrator.enums.PaymentStatus;
import com.example.paymentorchestrator.exception.GlobalExceptionHandler;
import com.example.paymentorchestrator.service.PaymentOrchestrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PaymentController.class)
@Import(GlobalExceptionHandler.class)
class PaymentControllerWebTest {

  @Autowired private MockMvc mvc;

  @MockBean private PaymentOrchestrationService service;

  @Test
  void createPaymentValidationErrorReturns400() throws Exception {
    mvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"currency\":\"INR\",\"method\":\"UPI\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  void createPaymentHappyPathReturns200() throws Exception {
    PaymentResponse resp = new PaymentResponse();
    resp.setId("id-1");
    resp.setAmount(new java.math.BigDecimal("250.50"));
    resp.setCurrency("INR");
    resp.setMethod(PaymentMethod.UPI);
    resp.setStatus(PaymentStatus.SUCCESS);
    resp.setPrimaryProvider("PROVIDER_B");
    resp.setFinalProvider("PROVIDER_B");

    when(service.createPayment(any())).thenReturn(resp);

    mvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":250.50,\"currency\":\"INR\",\"method\":\"UPI\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("id-1"));
  }

  @Test
  void fetchNotFoundReturns404() throws Exception {
    when(service.fetchPayment("missing"))
        .thenThrow(new com.example.paymentorchestrator.exception.PaymentNotFoundException("missing"));

    mvc.perform(get("/api/v1/payments/missing"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  void fetchHappyPathReturns200() throws Exception {
    PaymentResponse resp = new PaymentResponse();
    resp.setId("id-1");
    when(service.fetchPayment("id-1")).thenReturn(resp);

    mvc.perform(get("/api/v1/payments/id-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("id-1"));
  }
}


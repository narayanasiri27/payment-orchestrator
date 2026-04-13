package com.example.paymentorchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class PaymentOrchestratorApplication {
  public static void main(String[] args) {
    SpringApplication.run(PaymentOrchestratorApplication.class, args);
  }
}


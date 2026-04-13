package com.example.paymentorchestrator.dto;

import com.example.paymentorchestrator.enums.PaymentMethod;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePaymentRequest {
  @NotNull
  @DecimalMin(value = "0.01", inclusive = true)
  private BigDecimal amount;

  @NotBlank
  @Size(min = 3, max = 3)
  private String currency;

  @NotNull
  private PaymentMethod method;
}


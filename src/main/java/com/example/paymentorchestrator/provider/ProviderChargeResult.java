package com.example.paymentorchestrator.provider;

import lombok.Getter;

@Getter
public class ProviderChargeResult {
  private final boolean success;
  private final String providerReference;
  private final String failureReason;
  private final boolean retriable;

  private ProviderChargeResult(
      boolean success, String providerReference, String failureReason, boolean retriable) {
    this.success = success;
    this.providerReference = providerReference;
    this.failureReason = failureReason;
    this.retriable = retriable;
  }

  public static ProviderChargeResult success(String providerReference) {
    return new ProviderChargeResult(true, providerReference, null, false);
  }

  public static ProviderChargeResult failure(String reason, boolean retriable) {
    return new ProviderChargeResult(false, null, reason, retriable);
  }

  public boolean isSuccess() { return success; }
  public boolean isRetriable() { return retriable; }
}


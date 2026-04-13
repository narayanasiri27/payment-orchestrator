package com.example.paymentorchestrator.entity;

import com.example.paymentorchestrator.enums.PaymentMethod;
import com.example.paymentorchestrator.enums.PaymentProvider;
import com.example.paymentorchestrator.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class PaymentEntity {
  @Id
  @Column(name = "payment_id", nullable = false, updatable = false, length = 36)
  private String paymentId;

  @Column(name = "amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name = "currency", nullable = false)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(name = "method", nullable = false)
  private PaymentMethod method;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private PaymentStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider")
  private PaymentProvider provider;

  @Enumerated(EnumType.STRING)
  @Column(name = "primary_provider")
  private PaymentProvider primaryProvider;

  @Enumerated(EnumType.STRING)
  @Column(name = "final_provider")
  private PaymentProvider finalProvider;

  @Column(name = "provider_reference")
  private String providerReference;

  @Column(name = "failure_reason")
  private String failureReason;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  void ensureId() {
    if (this.paymentId == null || this.paymentId.trim().isEmpty()) {
      this.paymentId = UUID.randomUUID().toString();
    }
  }
}


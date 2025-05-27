package com.movie.main.entity;

import java.time.Instant;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class StripePayment {
    public static final int MaxLengthPaymentIntentId = 50;
    public static final int MinAnount = 1;

    public enum Status {
        REQUIRES_PAYMENT_METHOD("requires_payment_method"), REQUIRES_ACTION("requires_action"), PROCESSING(
                "processing"), REQUIRES_CAPTURE("requires_capture"), SUCCEEDED("succeeded"), CANCELED("canceled");

        @Nullable
        private final String stripeValue;

        Status(@Nullable final String stripeValue) {
            this.stripeValue = stripeValue;
        }

        @Nullable
        public static Status from(@Nullable final String status) {
            for (final var ps : values()) {
                if (ps.stripeValue.equals(status)) {
                    return ps;
                }
            }

            return null;
        }

        @Override
        @Nullable
        public String toString() {
            return this.stripeValue;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @Column(nullable = false, unique = true, length = MaxLengthPaymentIntentId)
    @Size(max = MaxLengthPaymentIntentId)
    @NotBlank
    private String paymentIntentId = "";

    @Nullable
    @Enumerated(EnumType.STRING)
    private Status status = null;

    @Column(nullable = false)
    @Min(MinAnount)
    private int amount = 0;

    @Column(nullable = false)
    @NotNull
    private Instant createdAt = Instant.now();

    public StripePayment(
            final String paymentIntentId,
            final Status status,
            final int amount,
            final Instant createdAt) {
        this.paymentIntentId = paymentIntentId;
        this.status = status;
        this.amount = amount;
        this.createdAt = createdAt;
    }
}

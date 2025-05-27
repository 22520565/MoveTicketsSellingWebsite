package com.movie.main.dto.request;

import java.util.Optional;

import com.movie.main.entity.StripePayment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record StripePaymentCreateIntentRequestDto(
        @Min(StripePayment.MinAnount) int amount,
        @Size(max = 255) String description) {
    public StripePaymentCreateIntentRequestDto {
        description = Optional.ofNullable(description).filter(s -> !s.isBlank()).orElse("Payment via Stripe");
    }
}

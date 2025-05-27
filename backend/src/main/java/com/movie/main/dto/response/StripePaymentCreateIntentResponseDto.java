package com.movie.main.dto.response;

public record StripePaymentCreateIntentResponseDto(
        String clientSecret) {
}

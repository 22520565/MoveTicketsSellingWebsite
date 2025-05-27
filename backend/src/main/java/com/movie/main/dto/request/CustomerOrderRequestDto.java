package com.movie.main.dto.request;

import java.time.LocalDate;

import com.movie.main.entity.CustomerOrder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerOrderRequestDto(
        @NotNull LocalDate date,
        @Size(min = CustomerOrder.VerifyCodeLength, max = CustomerOrder.VerifyCodeLength) @NotBlank String verifyCode,
        int totalPrice,
        int totalPriceAfterDiscount,
        int customerId) {
}

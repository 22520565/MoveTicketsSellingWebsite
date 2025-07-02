package com.movie.main.dto.response;

import java.time.LocalDate;

public record CustomerOrderResponseDto(
        int id,
        LocalDate date,
        String verifyCode,
        int totalPrice,
        int totalPriceAfterDiscount,
        int customerId) {}

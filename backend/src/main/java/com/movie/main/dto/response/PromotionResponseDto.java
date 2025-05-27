package com.movie.main.dto.response;

import java.time.LocalDate;

public record PromotionResponseDto(
        int id,
        String name,
        String thumbnailUrl,
        int discountRate,
        LocalDate beginDate,
        LocalDate endDate) {
}

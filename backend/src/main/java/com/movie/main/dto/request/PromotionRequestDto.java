package com.movie.main.dto.request;

import java.time.LocalDate;

import com.movie.main.entity.Promotion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PromotionRequestDto(@NotBlank String name,
        @Size(max = Promotion.MaxLengthThumbnailUrl) String thumbnailUrl, int discountRate, LocalDate beginDate,
        LocalDate endDate) {}

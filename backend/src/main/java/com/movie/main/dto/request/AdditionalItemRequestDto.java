package com.movie.main.dto.request;

import org.hibernate.validator.constraints.Range;

import com.movie.main.entity.AdditionalItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdditionalItemRequestDto(
        @NotBlank @Size(max = AdditionalItem.MaxLengthName) String name,
        @Min(0) int price,
        @Range(min = AdditionalItem.MinLoyalPointRate, max = AdditionalItem.MaxLoyalPointRate) int loyalPointRate,
        @Size(max = AdditionalItem.MaxLengthThumbnailUrl) String thumbnailUrl) {}

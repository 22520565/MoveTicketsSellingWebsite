package com.movie.main.dto.request;

import com.movie.main.entity.AdditionalItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdditionalItemRequestDto(@NotBlank @Size(max = AdditionalItem.MaxLengthName) String name,
        @Min(0) int price, @Size(max = AdditionalItem.MaxLengthThumbnailUrl) String thumbnailUrl) {}

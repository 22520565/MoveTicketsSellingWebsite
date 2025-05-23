package com.movie.main.dto.request;

import com.movie.main.entity.OrderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrderItemRequestDto(@NotBlank @Size(max = OrderItem.MaxLengthName) String name, @Min(0) int quantity,
        @Min(0) int price) {}

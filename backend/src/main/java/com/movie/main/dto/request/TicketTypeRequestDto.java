package com.movie.main.dto.request;

import org.hibernate.validator.constraints.Range;

import com.movie.main.entity.TicketType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TicketTypeRequestDto(
        @Size(max = TicketType.MaxLengthName) @NotBlank String title,
        @Min(1) int price,
        boolean isPair,
        @Range(min = TicketType.MinLoyalPointRate, max = TicketType.MaxLoyalPointRate) int loyalPointRate) {}

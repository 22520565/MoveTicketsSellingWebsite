package com.movie.main.dto.request;

import com.movie.main.entity.OrderTicket;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrderTicketRequestDto(
        @NotBlank @Size(max = OrderTicket.MaxLengthName) String name,
        @Min(0) int quantity,
        @Min(0) int price) {
}

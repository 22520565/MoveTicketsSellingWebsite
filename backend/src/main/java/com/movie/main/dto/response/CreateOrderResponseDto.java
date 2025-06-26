package com.movie.main.dto.response;

import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.FieldNameConstants.Include;

@Data
@AllArgsConstructor
@FieldNameConstants
public class CreateOrderResponseDto {
    @Include
    private final int id;

    @Include
    private final int totalPrice;

    @Include
    private final int totalPriceAfterDiscount;

    @Include
    private final int filmShowId;

    @Include
    @NotNull
    private final Set<@NotNull TicketResponseDto> tickets;

    @Include
    @NotNull
    private final Set<@NotNull Integer> seatIds;

    @Include
    @NotNull
    private final Set<@NotNull ItemResponseDto> items;

    @Include
    @NotNull
    private final Set<@NotNull Integer> promotionIds;

    @Include
    @Min(0)
    private final int pointUsage;

    @Data
    @AllArgsConstructor
    @FieldNameConstants
    public static class TicketResponseDto {
        @Include
        private final int typeId;

        @Include
        private final int quantity;
    }

    @Data
    @AllArgsConstructor
    @FieldNameConstants
    public static class ItemResponseDto {
        @Include
        private final int id;

        @Include
        private final int quantity;
    }
}

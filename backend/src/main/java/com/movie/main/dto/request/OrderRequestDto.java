package com.movie.main.dto.request;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.FieldNameConstants.Include;

@Data
@AllArgsConstructor
@FieldNameConstants
public class OrderRequestDto {
    @Include
    private final int totalPrice;

    @Include
    private final int totalPriceAfterDiscount;

    @Include
    private final Integer filmShowId;

    @Include
    @NotNull
    private final Set<@NotNull TicketRequestDto> tickets;

    @Include
    @NotNull
    private final Set<@NotNull Integer> seatIds;

    @Include
    @NotNull
    private final Set<@NotNull ItemRequestDto> items;

    @Include
    @NotNull
    private final Set<@NotNull Integer> promotionIds;

    @Include
    @Min(0)
    private final int pointUsage;

    @Include
    @Min(0)
    private final int loyalPoint;

    @Data
    @FieldNameConstants
    public static class TicketRequestDto {
        @Include
        private final int typeId;

        @Include
        private final int quantity;

        @JsonCreator
        public TicketRequestDto(
                @JsonProperty("typeId") final int typeId,
                @JsonProperty("quantity") final int quantity) {
            this.typeId = typeId;
            this.quantity = quantity;
        }
    }

    @Data
    @FieldNameConstants
    public static class ItemRequestDto {
        @Include
        private final int id;

        @Include
        private final int quantity;

        @JsonCreator
        public ItemRequestDto(
                @JsonProperty("id") final int id,
                @JsonProperty("quantity") final int quantity) {
            this.id = id;
            this.quantity = quantity;
        }
    }
}

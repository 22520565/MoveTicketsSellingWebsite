package com.movie.main.dto;

import com.movie.main.entity.RoomSeat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoomSeatDto(
        @NotBlank @Size(min = RoomSeat.MinLengthName, max = RoomSeat.MaxLengthName) String name,
        @NotBlank @Size(min = RoomSeat.MinLengthType, max = RoomSeat.MaxLengthType) String type,
        int roomId)
        implements InterfaceDto {
}

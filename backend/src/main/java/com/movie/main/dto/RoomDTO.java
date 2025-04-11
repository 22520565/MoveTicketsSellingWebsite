package com.movie.main.dto;

import com.movie.main.entity.Room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoomDto(
        @NotBlank @Size(min = Room.MinLengthName, max = Room.MaxLengthName) String name,
        int numberOfSeatRow,
        int numberOfSeatColumn,
        int centerX1,
        int centerX2,
        int centerY1,
        int centerY2,
        String note,
        int theaterId)
        implements InterfaceDto {
}

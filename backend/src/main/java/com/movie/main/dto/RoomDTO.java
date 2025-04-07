package com.movie.main.dto;

import com.movie.main.entity.Room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoomDTO(
        @NotBlank @Size(min = Room.MinLengthName, max = Room.MaxLengthName) String name,
        int theaterId) {
}

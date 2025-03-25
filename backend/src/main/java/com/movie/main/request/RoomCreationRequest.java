package com.movie.main.request;

import com.movie.main.entity.Room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoomCreationRequest(
          @NotBlank @Size(min = Room.MinLengthName, max = Room.MaxLengthName) String name,
          @NotNull Integer theaterId) {
}

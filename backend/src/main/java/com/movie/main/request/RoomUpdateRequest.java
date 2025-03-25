package com.movie.main.request;

import com.movie.main.entity.Room;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public record RoomUpdateRequest(
        @Size(min = Room.MinLengthName, max = Room.MaxLengthName) String name,
        Integer theaterId) {

    @AssertTrue
    public boolean isValid() {
        return ((this.name != null) || (this.theaterId != null));
    }
}

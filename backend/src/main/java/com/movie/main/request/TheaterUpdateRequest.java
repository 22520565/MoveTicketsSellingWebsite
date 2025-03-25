package com.movie.main.request;

import com.movie.main.entity.Theater;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public record TheaterUpdateRequest(
        @Size(min = Theater.MinLengthName, max = Theater.MaxLengthName) String name,
        @Size(min = Theater.MinLengthAddress, max = Theater.MaxLengthAddress) String address) {

    @AssertTrue
    public boolean isValid() {
        return ((this.name != null) || (this.address != null));
    }
}

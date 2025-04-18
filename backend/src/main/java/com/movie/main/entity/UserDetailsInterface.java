package com.movie.main.entity;

import jakarta.validation.constraints.NotNull;

public interface UserDetailsInterface extends Identifiable<Integer> {
    @Override
    @NotNull
    default Integer getId() {
        return this.getUser().getId();
    }

    @NotNull
    User getUser();
}

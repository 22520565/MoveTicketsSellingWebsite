package com.movie.main.entity;

import jakarta.validation.constraints.NotNull;

public interface Identifiable<TKey> {
    @NotNull
    TKey getId();
}

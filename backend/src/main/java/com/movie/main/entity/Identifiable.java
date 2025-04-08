package com.movie.main.entity;

import jakarta.annotation.Nonnull;

public interface Identifiable<TKey> {
    @Nonnull
    TKey getId();
}

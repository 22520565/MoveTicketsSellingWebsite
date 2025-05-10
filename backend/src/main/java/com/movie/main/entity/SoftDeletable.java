package com.movie.main.entity;

public interface SoftDeletable {
    boolean isDeleted();

    void setDeleted(final boolean deleted);
}

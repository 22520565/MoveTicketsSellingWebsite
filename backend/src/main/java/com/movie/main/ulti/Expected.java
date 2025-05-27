package com.movie.main.ulti;

import java.util.function.Function;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.function.Consumer;

public final class Expected<T, E> {
    private final T value;

    private final E error;

    private Expected(
            @Nullable
            final T value,

            @Nullable
            final E error) {
        this.value = value;
        this.error = error;
    }

    public static <T, E> Expected<T, E> success(
            @NotNull
            final T value) {
        return new Expected<>(value, null);
    }

    public static <T, E> Expected<T, E> failure(
            @NotNull
            final E error) {
        return new Expected<>(null, error);
    }

    public boolean isSuccess() {
        return (this.value != null);
    }

    public boolean isFailure() {
        return (this.error != null);
    }

    @Nullable
    public T getValue() {
        return this.value;
    }

    @Nullable
    public E getError() {
        return this.error;
    }

    public T valueOr(final T defaultValue) {
        if (!this.isSuccess()) {
            return defaultValue;
        }

        return this.value;
    }

    public <U> Expected<U, E> map(final Function<? super T, ? extends U> f) {
        if (!this.isSuccess()) {
            return Expected.failure(this.error);
        }

        return Expected.success(f.apply(this.value));
    }

    public <F> Expected<T, F> mapError(final Function<? super E, ? extends F> f) {
        if (!isFailure()) {
            return Expected.success(this.value);
        }

        return Expected.failure(f.apply(this.error));
    }

    public void ifSuccess(final Consumer<? super T> action) {
        if (isSuccess()) {
            action.accept(this.value);
        }
    }

    public void ifFailure(final Consumer<? super E> action) {
        if (isFailure()) {
            action.accept(this.error);
        }
    }

    @Override
    public String toString() {
        if (!this.isSuccess()) {
            return String.format("Failure(%s)", error);
        }

        return String.format("Success(%s)", value);
    }
}

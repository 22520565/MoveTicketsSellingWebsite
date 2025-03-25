package com.movie.main.entity;

import java.util.concurrent.atomic.AtomicBoolean;

import com.movie.main.request.MovieCreationRequest;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.experimental.FieldNameConstants;

@Entity
@Table
@FieldNameConstants
public final class Movie {
    public static final String DefaultName = "Default Name";
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 100;
    public static final int MinLengthDescription = 1;
    public static final int MaxLengthDescription = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    private Integer id = 0;

    @Column(length = MaxLengthName, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = DefaultName;

    @Column(length = MaxLengthDescription)
    @NotBlank
    @Size(min = MinLengthDescription, max = MaxLengthDescription)
    private String description = null;

    protected Movie() {
    }

    private Movie(final String name, final String description, final AtomicBoolean succeed) {
        succeed.set(this.setName(name) && this.setDescription(description));
    }

    @Nullable
    public static Movie create(final String name, final String description) {
        try {
            final var succeed = new AtomicBoolean(false);
            final var newMovie = new Movie(name, description, succeed);

            if (succeed.get()) {
                return newMovie;
            } else {
                return null;
            }
        } catch (final Exception exception) {
            return null;
        }
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean setName(final String newName) {
        if ((newName == null) || (newName.isBlank())) {
            return false;
        }

        final var newNameLength = newName.length();
        if ((newNameLength < MinLengthName) || (newNameLength > MaxLengthName)) {
            return false;
        }

        this.name = newName;
        return true;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean setDescription(final String newDescription) {
        if ((newDescription == null) || (newDescription.isBlank())) {
            return false;
        }

        final var newDescriptionLength = newDescription.length();
        if ((newDescriptionLength < MinLengthDescription) || (newDescriptionLength > MaxLengthDescription)) {
            return false;
        }

        this.description = newDescription;
        return true;
    }

}

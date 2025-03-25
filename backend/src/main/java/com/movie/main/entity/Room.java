package com.movie.main.entity;

import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table
@FieldNameConstants
@Slf4j
public final class Room {
    public static final String DefaultName = "Default Room Name";
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    private Integer id = null;

    @Column(length = MaxLengthName, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = DefaultName;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Theater theater = null;

    protected Room() {
    }

    private Room(final String name, final Theater theater, @NotNull final AtomicBoolean succeed) {
        succeed.set(this.setName(name) && this.setTheater(theater));
    }

    @Nullable
    public static Room create(final String name, final Theater theater) {
        try {
            final var succeed = new AtomicBoolean(false);
            final var newRoom = new Room(name, theater, succeed);

            if (succeed.get()) {
                return newRoom;
            } else {
                return null;
            }
        } catch (final Exception exception) {
            log.error(null, exception);
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

        var newNameLength = newName.length();
        if ((newNameLength < MinLengthName) || (newNameLength > MaxLengthName)) {
            return false;
        }

        this.name = newName;
        return true;
    }

    public Theater getTheater() {
        return this.theater;
    }

    public boolean setTheater(final Theater newTheater) {
        if (newTheater == null) {
            return false;
        }

        this.theater = newTheater;
        return true;
    }
}

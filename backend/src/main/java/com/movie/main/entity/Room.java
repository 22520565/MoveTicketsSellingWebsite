package com.movie.main.entity;

import java.lang.annotation.Annotation;
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
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table
@FieldNameConstants
public final class Room implements Identifiable<Integer> {
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.NONE)
    private Integer id = null;

    @Column(length = MaxLengthName, nullable = false, unique = true)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = null;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Theater theater = null;

    public Room(final String name, final Theater theater) {
        this.name = name;
        this.theater = theater;
    }
}

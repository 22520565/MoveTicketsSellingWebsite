package com.movie.main.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public final class Room extends AbstractIntegerIdentifiableEntity {
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 30;
    public static final int MinLengthNote = 1;
    public static final int MaxLengthNote = 100;

    @Column(length = MaxLengthName, nullable = false)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = "";

    @Column(nullable = false)
    @Min(0)
    private int numberOfSeatRow = 0;

    @Column(nullable = false)
    @Min(0)
    private int numberOfSeatColumn = 0;

    @Column(nullable = false)
    @Min(0)
    private int centerX1 = 0;

    @Column(nullable = false)
    @Min(0)
    private int centerX2 = 0;

    @Column(nullable = false)
    @Min(0)
    private int centerY1 = 0;

    @Column(nullable = false)
    @Min(0)
    private int centerY2 = 0;

    @Column(nullable = false)
    @Size(min = MinLengthNote, max = MaxLengthNote)
    @NotBlank
    private String note = "";

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Theater theater = null;

    @Column(nullable = false)
    private boolean deleted = false;

    public Room(final String name, final int numberOfSeatRow, final int numberOfSeatColumn, final int centerX1,
            final int centerX2, final int centerY1, final int centerY2, final String note, final Theater theater) {
        this.name = name;
        this.numberOfSeatRow = numberOfSeatRow;
        this.numberOfSeatColumn = numberOfSeatColumn;
        this.centerX1 = centerX1;
        this.centerX2 = centerX2;
        this.centerY1 = centerY1;
        this.centerY2 = centerY2;
        this.note = note;
        this.theater = theater;
    }
}

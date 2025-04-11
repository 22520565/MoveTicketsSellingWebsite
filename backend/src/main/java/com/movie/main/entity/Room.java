package com.movie.main.entity;

import com.movie.main.dto.RoomDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants
public final class Room implements Identifiable<Integer> {
    public static final int MinLengthName = 1;
    public static final int MaxLengthName = 30;
    public static final int MinLengthNote = 1;
    public static final int MaxLengthNote = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.NONE)
    private Integer id = null;

    @Column(length = MaxLengthName, nullable = false)
    @NotBlank
    @Size(min = MinLengthName, max = MaxLengthName)
    private String name = null;

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
    private String note = null;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Theater theater = null;

    @Column(nullable = false)
    private boolean deleted = false;

    public Room(@NotNull RoomDto dto, @NotNull Theater theater) {
        this.updateFromDto(dto, theater);
    }

    public void updateFromDto(@NotNull RoomDto dto, @NotNull Theater theater) {
        this.name = dto.name();
        this.numberOfSeatRow = dto.numberOfSeatRow();
        this.numberOfSeatColumn = dto.numberOfSeatColumn();
        this.centerX1 = dto.centerX1();
        this.centerX2 = dto.centerX2();
        this.centerY1 = dto.centerY1();
        this.centerY2 = dto.centerY2();
        this.note = dto.note();
        this.theater = theater;
    }
}

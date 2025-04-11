package com.movie.main.entity;

import java.time.LocalDateTime;

import com.movie.main.dto.FilmShowRequestDto;

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
public final class FilmShow implements Identifiable<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.NONE)
    private Integer id = null;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Film film = null;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private RoomSeat roomSeat = null;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime showTime = null;

    @Column(nullable = false)
    @NotBlank
    private String type = null;

    @Column(nullable = false)
    private boolean deleted = false;

    public FilmShow(
            final Film film,
            final RoomSeat roomSeat,
            final LocalDateTime showTime,
            final String type) {
        this.film = film;
        this.roomSeat = roomSeat;
        this.showTime = showTime;
        this.type = type;
    }
}

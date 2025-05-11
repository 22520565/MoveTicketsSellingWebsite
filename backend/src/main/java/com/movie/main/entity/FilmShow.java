package com.movie.main.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class FilmShow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    @NotNull
    private int id = 0;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Film film = null;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Room room = null;

    @Column(nullable = false)
    @NotNull
    private LocalDate showDate = LocalDate.now();

    @Column(nullable = false)
    @NotNull
    private LocalTime showTime = LocalTime.now();

    @Column(nullable = false)
    @NotBlank
    private String type = "";

    @Column(nullable = false)
    private boolean deleted = false;

    public FilmShow(final Film film, final Room room, final LocalDate showDate, final LocalTime showTime,
            final String type) {
        this.film = film;
        this.room = room;
        this.showDate = showDate;
        this.showTime = showTime;
        this.type = type;
    }
}

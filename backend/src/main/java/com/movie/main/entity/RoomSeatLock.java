package com.movie.main.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.hibernate.annotations.NaturalId;

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
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class RoomSeatLock {
    public static final int AmountValidMinutes = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id = 0;

    @NaturalId
    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private RoomSeat roomSeat = null;

    @NaturalId
    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private FilmShow filmShow = null;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime expireAt = LocalDateTime.now().plus(Duration.ofMinutes(AmountValidMinutes));

    @Column(nullable = false)
    @NotBlank
    private String sessionId;

    public RoomSeatLock(
            final RoomSeat roomSeat,
            final FilmShow filmShow,
            final String sessionId) {
        this.roomSeat = roomSeat;
        this.filmShow = filmShow;
        this.sessionId = sessionId;
    }
}

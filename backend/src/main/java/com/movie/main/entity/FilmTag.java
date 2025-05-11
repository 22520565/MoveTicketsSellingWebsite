package com.movie.main.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
public class FilmTag {
    @EmbeddedId
    @Setter(value = AccessLevel.PACKAGE)
    private FilmTagId id = new FilmTagId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = FilmTagId.Fields.filmId)
    @JoinColumn(nullable = false)
    @Setter(value = AccessLevel.NONE)
    @NotNull
    private Film film = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = FilmTagId.Fields.tagId)
    @JoinColumn(nullable = false)
    @Setter(value = AccessLevel.NONE)
    @NotNull
    private Tag tag = null;

    public FilmTag(@NotNull final Film film, @NotNull final Tag tag) {
        this.id = new FilmTagId(film.getId(), tag.getId());
        this.film = film;
        this.tag = tag;
    }

    public void setFilm(@NotNull final Film film) {
        this.id.setFilmId(film.getId());
        this.film = film;
    }

    public void setTag(@NotNull final Tag tag) {
        this.id.setTagId(tag.getId());
        this.tag = tag;
    }
}

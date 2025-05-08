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
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class FilmTag implements Identifiable<FilmTagId> {
    @EmbeddedId
    private FilmTagId id = new FilmTagId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = FilmTagId.Fields.filmId)
    @JoinColumn(nullable = false)
    @NotNull
    private Film film;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = FilmTagId.Fields.tagId)
    @JoinColumn(nullable = false)
    @NotNull
    private Tag tag;

    public FilmTag(@NotNull final Film film, @NotNull final Tag tag) {
        this.id = new FilmTagId(film.getId(), tag.getId());
        this.film = film;
        this.tag = tag;
    }
}

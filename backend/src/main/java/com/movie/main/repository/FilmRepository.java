package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.dto.FilmDto;
import com.movie.main.entity.Film;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;

@Repository
public class FilmRepository extends AbstractRepository<Film, FilmDto, Integer> {
    protected FilmRepository(@NotNull final EntityManager entityManager) {
        super(entityManager, Film.class, FilmDto.class);
    }

    @Nullable
    public Film findByName(final String name) {
        return this.findSingleByField(Film.Fields.name, name);
    }
}

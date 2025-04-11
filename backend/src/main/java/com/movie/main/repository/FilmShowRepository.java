package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.dto.FilmShowDto;
import com.movie.main.entity.FilmShow;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;

@Repository
public class FilmShowRepository extends AbstractRepository<FilmShow, FilmShowDto, Integer> {
    protected FilmShowRepository(@NotNull EntityManager entityManager) {
        super(entityManager, FilmShow.class, FilmShowDto.class);
    }
}

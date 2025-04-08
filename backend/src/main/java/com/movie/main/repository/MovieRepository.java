package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.dto.MovieDTO;
import com.movie.main.entity.Movie;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.EntityManager;

@Repository
public class MovieRepository extends AbstractRepository<Movie, MovieDTO, Integer> {
    protected MovieRepository(final EntityManager entityManager) {
        super(entityManager, Movie.class, MovieDTO.class);
    }

    @Nullable
    public Movie findByName(final String name) {
        return this.findSingleByField(Movie.Fields.name, name);
    }
}

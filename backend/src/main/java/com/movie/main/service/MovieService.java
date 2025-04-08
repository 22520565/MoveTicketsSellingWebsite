package com.movie.main.service;

import com.movie.main.dto.MovieDTO;
import com.movie.main.entity.Movie;
import com.movie.main.repository.MovieRepository;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.annotation.Nonnull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MovieService extends AbstractService<Movie, MovieDTO, Integer> {
    @Nonnull
    private final MovieRepository repository;

    protected MovieService(@Nonnull final MovieRepository repository) {
        this.repository = repository;
    }

    @Override
    protected MovieRepository getRepository() {
        return this.repository;
    }

    @Override
    public Movie create(@Nonnull final MovieDTO dto) {
        final var newMovie = new Movie(dto.name(), dto.description());
        return this.create(newMovie);
    }

    @Override
    public UpdateStatus update(@Nonnull final Integer id, @Nonnull final MovieDTO dto) {
        final var movie = this.repository.findById(id);
        if (movie == null) {
            return UpdateStatus.EntityNotExistsError;
        }

        movie.setName(dto.name());
        movie.setDescription(dto.description());

        return this.update(movie);
    }
}

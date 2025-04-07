package com.movie.main.service;

import com.movie.main.dto.MovieDTO;
import com.movie.main.entity.Movie;
import com.movie.main.repository.MovieRepository;
import com.movie.main.service.enumclass.DeletionStatus;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MovieService {
    @Nonnull
    private final MovieRepository repository;

    protected MovieService(@Nonnull final MovieRepository repository) {
        this.repository = repository;
    }

    @Nullable
    public Movie findById(final int id) {
        return this.repository.findById(id);
    }

    @Nullable
    public MovieDTO findDataById(final int id) {
        return this.repository.findDataById(id);
    }

    @Nullable
    public Movie create(@Nonnull final MovieDTO dto) {
        try {
            final var newMovie = new Movie(dto.name(), dto.description());
            final var result = this.repository.add(newMovie);

            return result.getValue();
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    @Nonnull
    public UpdateStatus update(final int id, @Nonnull final MovieDTO dto) {
        try {
            final var movie = this.repository.findById(id);
            if (movie == null) {
                return UpdateStatus.EntityNotExistsError;
            }

            movie.setName(dto.name());
            movie.setDescription(dto.description());

            final var result = this.repository.update(movie);
            if (result.isSuccess()) {
                return UpdateStatus.Success;
            }

            return switch (result.getError()) {
                case EntityNotExists -> UpdateStatus.EntityNotExistsError;
                case Persistence, Unspecified -> UpdateStatus.UnspecifiedError;
                default -> UpdateStatus.UnspecifiedError;
            };
        } catch (final Exception exception) {
            log.error(null, exception);
            return UpdateStatus.UnspecifiedError;
        }
    }

    public DeletionStatus deleteById(final int id) {
        try {
            return switch (this.repository.deleteById(id)) {
                case Success -> DeletionStatus.Success;
                case EntityNotExistsError -> DeletionStatus.EntityNotExistsError;
                case UnspecifiedError -> DeletionStatus.UnspecifiedError;
                default -> DeletionStatus.UnspecifiedError;
            };
        } catch (final Exception exception) {
            return DeletionStatus.UnspecifiedError;
        }
    }

}

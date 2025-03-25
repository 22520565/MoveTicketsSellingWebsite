package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.entity.Movie;
import com.movie.main.repository.MovieRepository;
import com.movie.main.request.MovieCreationRequest;
import com.movie.main.request.MovieUpdateRequest;

@Service
public final class MovieService {
    private final MovieRepository repository;

    public MovieService(final MovieRepository repository) {
        this.repository = repository;
    }

    public Movie findById(final Integer id) {
        if ((id == null) || (this.repository == null)) {
            return null;
        }

        try {
            return this.repository.findById(id).orElse(null);
        } catch (final Throwable throwable) {
            return null;
        }
    }

    public boolean create(final MovieCreationRequest request) {
        if ((request == null) || (this.repository == null)) {
            return false;
        }

        try {
            final var newMovie = Movie.create(request.name(), request.description());
            if (newMovie == null) {
                return false;
            }

            this.repository.save(newMovie);
        } catch (final Throwable throwable) {
            return false;
        }

        return true;
    }

    public boolean update(final Integer id, final MovieUpdateRequest request) {
        if ((id == null) || (request == null) || (this.repository == null)) {
            return false;
        }

        try {
            final var movie = this.repository.findById(id).orElse(null);
            if (movie == null) {
                return false;
            }

            var newMovieName = request.name();
            if (newMovieName != null) {
                movie.setName(newMovieName);
            }

            var newMovieDescription = request.description();
            if (newMovieDescription != null) {
                movie.setDescription(newMovieDescription);
            }

            this.repository.save(movie);
        } catch (final Throwable throwable) {
            return false;
        }

        return true;
    }

    public boolean deleteById(final Integer id) {
        if ((id == null) || (this.repository == null)) {
            return false;
        }

        try {
            this.repository.deleteById(id);
        } catch (final Throwable throwable) {
            return false;
        }

        return true;
    }

}

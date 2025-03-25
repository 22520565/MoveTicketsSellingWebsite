package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.entity.Theater;
import com.movie.main.repository.TheaterRepository;
import com.movie.main.request.TheaterCreationRequest;
import com.movie.main.request.TheaterUpdateRequest;

import jakarta.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public final class TheaterService {
    private final TheaterRepository repository;

    public TheaterService(final TheaterRepository repository) {
        this.repository = repository;
    }

    @Nullable
    public Theater findById(final Integer id) {
        if ((id == null) || (this.repository == null)) {
            return null;
        }

        try {
            return this.repository.findById(id).orElse(null);
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    public boolean create(final TheaterCreationRequest request) {
        if ((request == null) || (this.repository == null)) {
            return false;
        }

        final var newTheater = Theater.create(request.name(), request.address());
        if (newTheater == null) {
            return false;
        }

        try {
            this.repository.save(newTheater);
        } catch (final Exception exception) {
            log.error(null, exception);
            return false;
        }

        return true;
    }

    public boolean update(final Integer id, final TheaterUpdateRequest request) {
        if ((id == null) || (request == null) || (this.repository == null)) {
            return false;
        }

        try {
            final var theater = this.repository.findById(id).orElse(null);
            if (theater == null) {
                return false;
            }

            final var newTheaterName = request.name();
            if ((newTheaterName == null) || theater.setName(newTheaterName)) {
                return false;
            }

            final var newTheaterAddress = request.address();
            if ((newTheaterAddress == null) || theater.setAddress(newTheaterAddress)) {
                return false;
            }

            this.repository.save(theater);
        } catch (final Exception exception) {
            log.error(null, exception);
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
        } catch (final Exception exception) {
            log.error(null, exception);
            return false;
        }

        return true;
    }
}

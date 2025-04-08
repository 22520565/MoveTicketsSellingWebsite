package com.movie.main.service;

import com.movie.main.dto.TheaterDTO;
import com.movie.main.entity.Theater;
import com.movie.main.repository.TheaterRepository;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.annotation.Nonnull;

import org.springframework.stereotype.Service;

@Service
public final class TheaterService extends AbstractService<Theater, TheaterDTO, Integer> {
    @Nonnull
    private final TheaterRepository repository;

    protected TheaterService(@Nonnull final TheaterRepository repository) {
        this.repository = repository;
    }

    @Override
    protected TheaterRepository getRepository() {
        return this.repository;
    }

    @Override
    public Theater create(@Nonnull final TheaterDTO dto) {
        final var theater = new Theater(dto.name(), dto.address());
        return this.create(theater);
    }

    @Override
    public UpdateStatus update(@Nonnull final Integer id, @Nonnull final TheaterDTO dto) {
        final var theater = this.repository.findById(id);
        if (theater == null) {
            return UpdateStatus.EntityNotExistsError;
        }

        theater.setName(dto.name());
        theater.setAddress(dto.address());

        return this.update(theater);
    }
}

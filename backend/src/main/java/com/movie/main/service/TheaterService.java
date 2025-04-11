package com.movie.main.service;

import com.movie.main.dto.TheaterDto;
import com.movie.main.entity.Theater;
import com.movie.main.repository.TheaterRepository;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

@Service
public class TheaterService extends AbstractService<Theater, TheaterDto, Integer> {
    @NotNull
    private final TheaterRepository repository;

    protected TheaterService(@NotNull final TheaterRepository repository) {
        this.repository = repository;
    }

    @Override
    public Theater create(@NotNull final TheaterDto dto) {
        final var theater = new Theater(dto.name(), dto.address());
        return this.create(theater);
    }

    @Override
    public UpdateStatus update(@NotNull final Integer id, @NotNull final TheaterDto dto) {
        final var theater = this.repository.findById(id);
        if (theater == null) {
            return UpdateStatus.EntityNotExistsError;
        }

        theater.setName(dto.name());
        theater.setAddress(dto.address());

        return this.update(theater);
    }

    @Override
    protected TheaterRepository getRepository() {
        return this.repository;
    }
}

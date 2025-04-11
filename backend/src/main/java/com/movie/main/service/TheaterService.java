package com.movie.main.service;

import com.movie.main.dto.TheaterRequestDto;
import com.movie.main.entity.Theater;
import com.movie.main.repository.TheaterRepository;
import com.movie.main.ulti.Expected;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

@Service
public class TheaterService extends AbstractService<TheaterRequestDto, Theater, Integer> {
    @NotNull
    private final TheaterRepository repository;

    protected TheaterService(@NotNull final TheaterRepository repository) {
        this.repository = repository;
    }

    @Override
    public Theater create(@NotNull final TheaterRequestDto requestDto) {
        final var theater = new Theater(
                requestDto.name(),
                requestDto.address());

        return this.save(theater);
    }

    @Override
    public Expected<Theater, UpdateError> update(@NotNull final Integer id,
            @NotNull final TheaterRequestDto requestDto) {
        var theater = this.findById(id);
        if (theater == null) {
            return Expected.failure(UpdateError.EntityNotExists);
        }

        theater.setName(requestDto.name());
        theater.setAddress(requestDto.address());

        theater = this.save(theater);
        if (theater == null) {
            return Expected.failure(UpdateError.Unspecified);
        }

        return Expected.success(theater);
    }

    @Override
    protected TheaterRepository getRepository() {
        return this.repository;
    }
}

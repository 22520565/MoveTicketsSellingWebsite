package com.movie.main.service;

import com.movie.main.dto.request.TheaterRequestDto;
import com.movie.main.dto.response.TheaterResponseDto;
import com.movie.main.entity.Theater;
import com.movie.main.repository.TheaterRepository;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

@Service
public class TheaterService extends AbstractService<TheaterRequestDto, TheaterResponseDto, Theater, Integer> {
    @NotNull
    private final TheaterRepository repository;

    protected TheaterService(@NotNull final TheaterRepository repository) {
        this.repository = repository;
    }

    @Override
    protected TheaterResponseDto createResponseDtoFromEntity(@NotNull final Theater entity) {
        return new TheaterResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getAddress());
    }

    @Override
    protected Theater createEntityFromRequestDto(@NotNull final TheaterRequestDto requestDto) {
        return new Theater(
                requestDto.name(),
                requestDto.address());
    }

    @Override
    protected Theater updateEntityFromRequestDto(
            @NotNull final Theater entity,
            @NotNull final TheaterRequestDto requestDto) {
        entity.setName(requestDto.name());
        entity.setAddress(requestDto.address());

        return entity;
    }

    @Override
    protected TheaterRepository getRepository() {
        return this.repository;
    }
}

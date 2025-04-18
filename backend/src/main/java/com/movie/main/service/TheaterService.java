package com.movie.main.service;

import com.movie.main.dto.request.TheaterRequestDto;
import com.movie.main.dto.response.TheaterResponseDto;
import com.movie.main.entity.Theater;
import com.movie.main.repository.TheaterRepository;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

@Service
public class TheaterService extends AbstractEntityService<TheaterRequestDto, TheaterResponseDto, Theater, Integer> {
    @NotNull
    private final TheaterRepository repository;

    protected TheaterService(@NotNull final TheaterRepository repository) {
        this.repository = repository;
    }

    @Override
    protected TheaterResponseDto createResponseDtoFromEntity(@NotNull final Theater theater) {
        return new TheaterResponseDto(
                theater.getId(),
                theater.getName(),
                theater.getAddress());
    }

    @Override
    protected Theater createEntityFromRequestDto(@NotNull final TheaterRequestDto requestDto) {
        return new Theater(
                requestDto.name(),
                requestDto.address());
    }

    @Override
    protected Theater updateEntityFromRequestDto(
            @NotNull final Theater theater,
            @NotNull final TheaterRequestDto requestDto) {
        theater.setName(requestDto.name());
        theater.setAddress(requestDto.address());

        return theater;
    }

    @Override
    protected TheaterRepository getRepository() {
        return this.repository;
    }
}

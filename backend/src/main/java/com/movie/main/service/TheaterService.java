package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.TheaterRequestDto;
import com.movie.main.entity.Theater;
import com.movie.main.repository.TheaterRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TheaterService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    @NotNull
    private final TheaterRepository repository;

    public TheaterService(@NotNull final TheaterRepository repository) {
        this.repository = repository;
    }

    @NotNull
    public Page<@NotNull Theater> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @NotNull
    public Page<@NotNull Theater> findAllByFilmIdAndDeletedFalseOrderByShowDateTimeFromNow(final int filmId,
            @NotNull final PageRequest pageRequest) {
        return this.repository.findAllByFilmIdAndDeletedFalseOrderByShowDateTimeFromNow(filmId, pageRequest);
    }

    @Nullable
    public Theater findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<Theater, CreationError> create(@NotNull final TheaterRequestDto requestDto) {
        final var newTheater = new Theater(requestDto.name(), requestDto.address());

        try {
            return Expected.success(this.repository.save(newTheater));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Theater, UpdateError> updateById(final int id, @NotNull final TheaterRequestDto requestDto) {
        final var theater = this.findById(id);
        if (theater == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        theater.setName(requestDto.name());
        theater.setAddress(requestDto.address());

        try {
            return Expected.success(this.repository.save(theater));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public void deleteById(final int id) {
        this.repository.deleteById(id);
    }
}

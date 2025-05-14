package com.movie.main.service;

import com.movie.main.dto.request.FilmRequestDto;
import com.movie.main.entity.Film;
import com.movie.main.repository.FilmRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FilmService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum MarkDeletedStatusResult {
        SUCCESS, ENTITY_NOT_EXISTS_ERROR, UNSPECIFIED_ERROR,
    }

    @NotNull
    private final FilmRepository repository;

    protected FilmService(@NotNull final FilmRepository repository) {
        this.repository = repository;
    }

    @NotNull
    public Page<@NotNull Film> findAllByDeletedFalse(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedFalse(pageRequest);
    }

    @NotNull
    public Page<@NotNull Film> findAllByDeletedTrue(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedTrue(pageRequest);
    }

    public Page<@NotNull Film> searchAllFilmsWithTagsByDeletedFalse(String keyword,
            @NotNull final PageRequest pageRequest) {
        return this.repository.searchAllFilmsWithTagsByDeletedFalse(keyword, pageRequest);
    }

    @Nullable
    public Film findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Nullable
    public Film findByIdAndDeletedFalse(final int id) {
        return this.repository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public Film findByIdAndDeletedTrue(final int id) {
        return this.repository.findByIdAndDeletedTrue(id).orElse(null);
    }

    @NotNull
    public Expected<Film, CreationError> create(@NotNull final FilmRequestDto requestDto) {
        final var newFilm = new Film(requestDto.name(), requestDto.thumbnailUrl(), requestDto.trailerUrl(),
                requestDto.tags(), requestDto.duration(), requestDto.ageRestriction(), requestDto.voice(),
                requestDto.originatedCountry(), requestDto.is3D(), requestDto.content(), requestDto.beginDate());

        try {
            return Expected.success(this.repository.save(newFilm));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Film, UpdateError> updateByIdAndDeletedFalse(final int id,
            @NotNull final FilmRequestDto requestDto) {
        final var film = this.findByIdAndDeletedFalse(id);
        if (film == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        film.setName(requestDto.name());
        film.setThumbnailUrl(requestDto.thumbnailUrl());
        film.setTrailerUrl(requestDto.trailerUrl());
        film.setDuration(requestDto.duration());
        film.setAgeRestriction(requestDto.ageRestriction());
        film.setVoice(requestDto.voice());
        film.setOriginatedCountry(requestDto.originatedCountry());
        film.set3D(requestDto.is3D());
        film.setContent(requestDto.content());
        film.setBeginDate(requestDto.beginDate());

        try {
            return Expected.success(this.repository.save(film));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public MarkDeletedStatusResult markAsDeletedById(final int id) {
        return this.markDeletedStatusById(id, true);
    }

    @NotNull
    public MarkDeletedStatusResult markAsUndeletedById(final int id) {
        return this.markDeletedStatusById(id, false);
    }

    @NotNull
    public MarkDeletedStatusResult markDeletedStatusById(final int id, final boolean deletedStatusToMark) {
        final var film = this.findById(id);
        if (film == null) {
            return MarkDeletedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        film.setDeleted(deletedStatusToMark);

        try {
            this.repository.save(film);
            return MarkDeletedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkDeletedStatusResult.UNSPECIFIED_ERROR;
        }
    }
}

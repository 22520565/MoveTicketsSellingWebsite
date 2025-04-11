package com.movie.main.service;

import com.movie.main.dto.FilmRequestDto;
import com.movie.main.entity.Film;
import com.movie.main.repository.FilmRepository;
import com.movie.main.ulti.Expected;

import jakarta.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FilmService extends AbstractService<FilmRequestDto, Film, Integer> {
    @NotNull
    private final FilmRepository repository;

    @NotNull
    private final TagService tagService;

    protected FilmService(@NotNull final FilmRepository repository, @NotNull final TagService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

    @Override
    public Film create(@NotNull final FilmRequestDto requestDto) {
        final var tag = this.tagService.findById(requestDto.tagId());
        if (tag == null) {
            return null;
        }

        final var newFilm = new Film(
                requestDto.name(),
                requestDto.thumbnailUrl(),
                requestDto.trailerUrl(),
                tag,
                requestDto.duration(),
                requestDto.ageRestriction(),
                requestDto.voice(),
                requestDto.originatedCountry(),
                requestDto.is3D(),
                requestDto.content(),
                requestDto.beginDate());

        return this.save(newFilm);
    }

    @Override
    public Expected<Film, UpdateError> update(@NotNull final Integer id, @NotNull final FilmRequestDto requestDto) {
        var film = this.findById(id);
        if (film == null) {
            return Expected.failure(UpdateError.EntityNotExists);
        }

        var tag = film.getTag();
        if (tag.getId() != requestDto.tagId()) {
            tag = this.tagService.findById(requestDto.tagId());
            if (tag == null) {
                return Expected.failure(UpdateError.EntityNotExists);
            }
        }

        film.setName(requestDto.name());
        film.setThumbnailUrl(requestDto.thumbnailUrl());
        film.setTrailerUrl(requestDto.trailerUrl());
        film.setTag(tag);
        film.setDuration(requestDto.duration());
        film.setAgeRestriction(requestDto.ageRestriction());
        film.setVoice(requestDto.voice());
        film.setOriginatedCountry(requestDto.originatedCountry());
        film.set3D(requestDto.is3D());
        film.setContent(requestDto.content());
        film.setBeginDate(requestDto.beginDate());

        film = this.save(film);
        if (film == null) {
            return Expected.failure(UpdateError.Unspecified);
        }

        return Expected.success(film);
    }

    @Override
    protected FilmRepository getRepository() {
        return this.repository;
    }

}

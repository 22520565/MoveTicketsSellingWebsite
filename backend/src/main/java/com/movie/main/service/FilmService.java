package com.movie.main.service;

import com.movie.main.dto.request.FilmRequestDto;
import com.movie.main.dto.response.FilmResponseDto;
import com.movie.main.entity.Film;
import com.movie.main.repository.FilmRepository;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

@Service
public class FilmService extends AbstractEntityService<FilmRequestDto, FilmResponseDto, Film, Integer> {
    @NotNull
    private final FilmRepository repository;

    @NotNull
    private final TagService tagService;

    protected FilmService(@NotNull final FilmRepository repository, @NotNull final TagService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

    @Override
    protected FilmResponseDto createResponseDtoFromEntity(@NotNull final Film film) {
        return new FilmResponseDto(
                film.getId(),
                film.getName(),
                film.getThumbnailUrl(),
                film.getTrailerUrl(),
                film.getTag().getId(),
                film.getDuration(),
                film.getAgeRestriction(),
                film.getVoice(),
                film.getOriginatedCountry(),
                film.is3D(),
                film.getDescription(),
                film.getContent(),
                film.getBeginDate());
    }

    @Override
    protected Film createEntityFromRequestDto(@NotNull final FilmRequestDto requestDto) {
        final var tag = this.tagService.findEntityById(requestDto.tagId());
        if (tag == null) {
            return null;
        }

        return new Film(
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
    }

    @Override
    protected Film updateEntityFromRequestDto(
            @NotNull final Film film,
            @NotNull final FilmRequestDto requestDto) {
        var tag = film.getTag();
        if (tag.getId() != requestDto.tagId()) {
            tag = this.tagService.findEntityById(requestDto.tagId());
            if (tag == null) {
                return null;
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

        return film;
    }

    @Override
    protected FilmRepository getRepository() {
        return this.repository;
    }

}

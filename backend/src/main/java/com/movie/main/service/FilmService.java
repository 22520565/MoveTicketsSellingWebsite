package com.movie.main.service;

import com.movie.main.dto.request.FilmRequestDto;
import com.movie.main.dto.response.FilmResponseDto;
import com.movie.main.entity.Film;
import com.movie.main.repository.FilmRepository;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

@Service
public class FilmService extends AbstractService<FilmRequestDto, FilmResponseDto, Film, Integer> {
    @NotNull
    private final FilmRepository repository;

    @NotNull
    private final TagService tagService;

    protected FilmService(@NotNull final FilmRepository repository, @NotNull final TagService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

    @Override
    protected FilmResponseDto createResponseDtoFromEntity(@NotNull final Film entity) {
        return new FilmResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getThumbnailUrl(),
                entity.getTrailerUrl(),
                entity.getTag().getId(),
                entity.getDuration(),
                entity.getAgeRestriction(),
                entity.getVoice(),
                entity.getOriginatedCountry(),
                entity.is3D(),
                entity.getDescription(),
                entity.getContent(),
                entity.getBeginDate());
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
            @NotNull final Film entity,
            @NotNull final FilmRequestDto requestDto) {
        var tag = entity.getTag();
        if (tag.getId() != requestDto.tagId()) {
            tag = this.tagService.findEntityById(requestDto.tagId());
            if (tag == null) {
                return null;
            }
        }

        entity.setName(requestDto.name());
        entity.setThumbnailUrl(requestDto.thumbnailUrl());
        entity.setTrailerUrl(requestDto.trailerUrl());
        entity.setTag(tag);
        entity.setDuration(requestDto.duration());
        entity.setAgeRestriction(requestDto.ageRestriction());
        entity.setVoice(requestDto.voice());
        entity.setOriginatedCountry(requestDto.originatedCountry());
        entity.set3D(requestDto.is3D());
        entity.setContent(requestDto.content());
        entity.setBeginDate(requestDto.beginDate());

        return entity;
    }

    @Override
    protected FilmRepository getRepository() {
        return this.repository;
    }

}

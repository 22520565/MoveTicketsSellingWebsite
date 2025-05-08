package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.FilmTagRequestDto;
import com.movie.main.dto.response.FilmTagReponseDto;
import com.movie.main.entity.FilmTag;
import com.movie.main.entity.FilmTagId;
import com.movie.main.repository.FilmTagRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class FilmTagService extends AbstractEntityService<FilmTagRequestDto, FilmTagReponseDto, FilmTag, FilmTagId> {
    @NotNull
    private final FilmTagRepository repository;

    @NotNull
    private final FilmService filmService;

    @NotNull
    private final TagService tagService;

    public FilmTagService(@NotNull final FilmTagRepository repository, @NotNull final FilmService filmService,
            @NotNull final TagService tagService) {
        this.repository = repository;
        this.filmService = filmService;
        this.tagService = tagService;
    }

    @Override
    @NotNull
    protected FilmTagReponseDto createResponseDtoFromEntity(@NotNull final FilmTag filmTag) {
        return new FilmTagReponseDto(filmTag.getId());
    }

    @Override
    protected FilmTag createEntityFromRequestDto(@NotNull final FilmTagRequestDto requestDto) {
        final var filmTagId = requestDto.id();

        final var tag = this.tagService.findEntityById(filmTagId.getTagId());
        if (tag == null) {
            return null;
        }

        final var film = this.filmService.findEntityById(filmTagId.getFilmId());
        if (film == null) {
            return null;
        }

        return new FilmTag(film, tag);
    }

    @Override
    protected FilmTag updateEntityFromRequestDto(@NotNull FilmTag filmTag, @NotNull FilmTagRequestDto requestDto) {
        final var filmTagId = requestDto.id();

        final var tag = this.tagService.findEntityById(filmTagId.getTagId());
        if (tag == null) {
            return null;
        }

        final var film = this.filmService.findEntityById(filmTagId.getFilmId());
        if (film == null) {
            return null;
        }

        filmTag.setFilm(film);
        filmTag.setTag(tag);

        return filmTag;
    }

    @Override
    @NotNull
    protected FilmTagRepository getRepository() {
        return this.repository;
    }

}

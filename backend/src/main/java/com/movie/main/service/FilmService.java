package com.movie.main.service;

import com.movie.main.dto.FilmDto;
import com.movie.main.entity.Film;
import com.movie.main.repository.FilmRepository;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FilmService extends AbstractService<Film, FilmDto, Integer> {
    @NotNull
    private final FilmRepository repository;

    @NotNull
    private final TagService tagService;

    protected FilmService(@NotNull final FilmRepository repository, @NotNull final TagService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

    @Override
    public Film create(@NotNull final FilmDto dto) {
        final var tag = this.tagService.getRepository().findById(dto.tagId());
        if (tag == null) {
            return null;
        }

        final var newMovie = new Film(dto, tag);
        return this.create(newMovie);
    }

    @Override
    public UpdateStatus update(@NotNull final Integer id, @NotNull final FilmDto dto) {
        final var movie = this.repository.findById(id);
        if (movie == null) {
            return UpdateStatus.EntityNotExistsError;
        }

        var tag = movie.getTag();
        if (tag.getId() != dto.tagId()) {
            tag = this.tagService.getRepository().findById(dto.tagId());
            if (tag == null) {
                return UpdateStatus.EntityNotExistsError;
            }
        }

        movie.updateFromDto(dto, tag);
        return this.update(movie);
    }

    @Override
    protected FilmRepository getRepository() {
        return this.repository;
    }

}

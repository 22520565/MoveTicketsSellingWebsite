package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.FilmShowRequestDto;
import com.movie.main.dto.response.FilmShowResponseDto;
import com.movie.main.entity.FilmShow;
import com.movie.main.service.FilmShowService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/film-shows")
public class FilmShowController
        extends AbstractEntityController<FilmShowRequestDto, FilmShowResponseDto, FilmShow, Integer> {
    @NotNull
    private final FilmShowService filmShowService;

    protected FilmShowController(@NotNull final FilmShowService filmShowService) {
        this.filmShowService = filmShowService;
    }

    @Override
    protected FilmShowService getService() {
        return this.filmShowService;
    }

}

package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.FilmShowDto;
import com.movie.main.entity.FilmShow;
import com.movie.main.service.FilmShowService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("film-shows")
public class FilmShowController extends AbstractController<FilmShow, FilmShowDto, Integer> {
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

package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.FilmRequestDto;
import com.movie.main.entity.Film;
import com.movie.main.service.FilmService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("films")
public class FilmController extends AbstractController<FilmRequestDto, Film, Integer> {
    @NotNull
    private final FilmService service;

    protected FilmController(@NotNull final FilmService service) {
        this.service = service;
    }

    @Override
    protected final FilmService getService() {
        return this.service;
    }
}

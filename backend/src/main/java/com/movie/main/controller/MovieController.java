package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.MovieDTO;
import com.movie.main.entity.Movie;
import com.movie.main.service.MovieService;

import jakarta.annotation.Nonnull;

@RestController
@RequestMapping("movies")
public class MovieController extends AbstractController<Movie, MovieDTO, Integer> {
    @Nonnull
    private final MovieService service;

    protected MovieController(@Nonnull final MovieService service) {
        this.service = service;
    }

    @Override
    protected final MovieService getService() {
        return this.service;
    }
}

package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.TheaterDTO;
import com.movie.main.entity.Theater;
import com.movie.main.service.TheaterService;

import jakarta.annotation.Nonnull;

@RestController
@RequestMapping("theaters")
public class TheaterController extends AbstractController<Theater, TheaterDTO, Integer> {
    @Nonnull
    private final TheaterService service;

    protected TheaterController(@Nonnull final TheaterService service) {
        this.service = service;
    }

    @Override
    protected TheaterService getService() {
        return this.service;
    }
}

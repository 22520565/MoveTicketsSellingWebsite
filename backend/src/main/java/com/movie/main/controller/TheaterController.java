package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.TheaterRequestDto;
import com.movie.main.entity.Theater;
import com.movie.main.service.TheaterService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("theaters")
public class TheaterController extends AbstractController<TheaterRequestDto, Theater, Integer> {
    @NotNull
    private final TheaterService service;

    protected TheaterController(@NotNull final TheaterService service) {
        this.service = service;
    }

    @Override
    protected TheaterService getService() {
        return this.service;
    }
}

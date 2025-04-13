package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.RoomSeatRequestDto;
import com.movie.main.dto.response.RoomSeatResponseDto;
import com.movie.main.entity.RoomSeat;
import com.movie.main.service.RoomSeatService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("room-seats")
public class RoomSeatController extends AbstractController<RoomSeatRequestDto, RoomSeatResponseDto, RoomSeat, Integer> {
    @NotNull
    private final RoomSeatService service;

    protected RoomSeatController(@NotNull final RoomSeatService service) {
        this.service = service;
    }

    @Override
    protected RoomSeatService getService() {
        return this.service;
    }
}

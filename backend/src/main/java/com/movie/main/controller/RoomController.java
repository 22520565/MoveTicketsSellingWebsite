package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.RoomRequestDto;
import com.movie.main.dto.response.RoomResponseDto;
import com.movie.main.entity.Room;
import com.movie.main.service.RoomService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("rooms")
public class RoomController extends AbstractController<RoomRequestDto, RoomResponseDto, Room, Integer> {
    @NotNull
    private final RoomService service;

    protected RoomController(@NotNull final RoomService service) {
        this.service = service;
    }

    @Override
    protected final RoomService getService() {
        return this.service;
    }
}

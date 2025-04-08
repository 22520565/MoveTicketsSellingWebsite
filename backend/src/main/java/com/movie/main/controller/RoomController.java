package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.RoomDTO;
import com.movie.main.entity.Room;
import com.movie.main.service.RoomService;

import jakarta.annotation.Nonnull;

@RestController
@RequestMapping("rooms")
public class RoomController extends AbstractController<Room, RoomDTO, Integer> {
    @Nonnull
    private final RoomService service;

    protected RoomController(@Nonnull final RoomService service) {
        this.service = service;
    }

    @Override
    protected final RoomService getService() {
        return this.service;
    }
}

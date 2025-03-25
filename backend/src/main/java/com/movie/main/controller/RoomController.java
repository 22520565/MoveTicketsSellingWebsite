package com.movie.main.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.entity.Room;
import com.movie.main.request.RoomCreationRequest;
import com.movie.main.request.RoomUpdateRequest;
import com.movie.main.service.RoomService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("rooms")
@Slf4j
public final class RoomController {
    private final RoomService service;

    public RoomController(final RoomService service) {
        this.service = service;
    }

    @GetMapping("details/{idString}")
    public Room findById(@PathVariable String idString) {
        try {
            final var id = Integer.valueOf(idString);
            return service.findById(id);
        } catch (final Exception exception) {
            log.warn(null, exception);
            return null;
        }
    }

    @PostMapping("create")
    public boolean create(@RequestBody @Valid final RoomCreationRequest request) {
        try {
            return this.service.create(request);
        } catch (final Exception exception) {
            log.warn(null, exception);
            return false;
        }
    }

    @PutMapping("update/{idString}")
    public boolean update(@PathVariable String idString, @RequestBody RoomUpdateRequest request) {
        try {
            final var id = Integer.valueOf(idString);
            return this.service.update(id, request);
        } catch (final Exception exception) {
            log.warn(null, exception);
            return false;
        }
    }

    @DeleteMapping("delete/{idString}")
    public boolean deleteById(@PathVariable String idString) {
        try {
            final var id = Integer.valueOf(idString);
            return this.service.deleteById(id);
        } catch (final Exception exception) {
            log.warn(null, exception);
            return false;
        }
    }

}

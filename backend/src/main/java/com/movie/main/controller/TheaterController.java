package com.movie.main.controller;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.TheaterDTO;
import com.movie.main.service.TheaterService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("theaters")
@Slf4j
public class TheaterController {
    private final TheaterService service;

    protected TheaterController(final TheaterService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public ResponseEntity<TheaterDTO> findDataById(@PathVariable final int id) {
        final var result = service.findDataById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid final TheaterDTO dto) {
        final var newTheater = this.service.create(dto);

        if (newTheater == null) {
            return ResponseEntity.internalServerError().build();
        }

        final var newTheaterId = newTheater.getId();
        final var location = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(TheaterController.class).findDataById(newTheaterId))
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable final int id, @RequestBody @Valid final TheaterDTO dto) {
        return switch (this.service.update(id, dto)) {
            case Success -> ResponseEntity.noContent().build();
            case EntityNotExistsError -> ResponseEntity.notFound().build();
            case UnspecifiedError -> ResponseEntity.internalServerError().build();
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable final int id) {
        return switch (this.service.deleteById(id)) {
            case Success -> ResponseEntity.noContent().build();
            case EntityNotExistsError -> ResponseEntity.notFound().build();
            case UnspecifiedError -> ResponseEntity.internalServerError().build();
            default -> ResponseEntity.internalServerError().build();
        };
    }
}

package com.movie.main.controller;

import com.movie.main.dto.InterfaceDto;
import com.movie.main.entity.Identifiable;
import com.movie.main.service.AbstractService;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractController<TRequestDto, TEntity extends Identifiable<TKey>, TKey> {
    public static final String DEFAULT_PAGE_NUMBER_STRING = "0";
    public static final String DEFAULT_PAGE_SIZE_STRING = "10";
    public static final int MAX_PAGE_SIZE = 100;

    protected abstract AbstractService<TRequestDto, TEntity, TKey> getService();

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<TEntity>>> findAllData(
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER_STRING) @Min(value = 0) @Valid final int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE_STRING) @Range(min = 1, max = MAX_PAGE_SIZE) @Valid final int size,
            final PagedResourcesAssembler<TEntity> assembler) {
        final var movies = this.getService().findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("{id}")
    public ResponseEntity<TEntity> findById(@PathVariable @NotNull @Valid final TKey id) {
        final var result = this.getService().findById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @NotNull @Valid final TRequestDto requestDto) {
        final var newEntity = this.getService().create(requestDto);

        if (newEntity == null) {
            return ResponseEntity.internalServerError().build();
        }

        final var newEntityId = newEntity.getId();
        final var location = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).findById(newEntityId))
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<TEntity> update(@PathVariable @NotNull @Valid final TKey id,
            @RequestBody @Valid final TRequestDto requestDto) {
        final var result = this.getService().update(id, requestDto);

        final var newEntity = result.getValue();
        if (newEntity != null) {
            return ResponseEntity.ok(newEntity);
        }

        return switch (result.getError()) {
            case EntityNotExists -> ResponseEntity.notFound().build();
            case Unspecified -> ResponseEntity.internalServerError().build();
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @NotNull @Valid final TKey id) {
        return switch (this.getService().deleteById(id)) {
            case Success -> ResponseEntity.noContent().build();
            case EntityNotExistsError -> ResponseEntity.notFound().build();
            case UnspecifiedError -> ResponseEntity.internalServerError().build();
            default -> ResponseEntity.internalServerError().build();
        };
    }
}

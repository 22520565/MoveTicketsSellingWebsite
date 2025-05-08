package com.movie.main.controller;

import com.movie.main.auth.RequirePermissions;
import com.movie.main.config.OpenApiConfig;
import com.movie.main.dto.request.EntityRequestDtoInterface;
import com.movie.main.dto.response.EntityResponseDtoInterface;
import com.movie.main.entity.Identifiable;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.service.AbstractEntityService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@DenyAll
@Slf4j
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public abstract class AbstractEntityController<TRequestDto extends EntityRequestDtoInterface,
        TResponseDto extends EntityResponseDtoInterface<TKey>,
        TEntity extends Identifiable<TKey>,
        TKey> {
    public static final String DEFAULT_PAGE_NUMBER_STRING = "0";
    public static final String DEFAULT_PAGE_SIZE_STRING = "10";
    public static final int MAX_PAGE_SIZE = 100;

    @GetMapping
    @RequirePermissions(value = Permission.ADMIN)
    public ResponseEntity<PagedModel<EntityModel<TResponseDto>>> findAllData(
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER_STRING) @Min(value = 0) @Valid final int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE_STRING) @Range(min = 1, max = MAX_PAGE_SIZE) @Valid final int size,
            final PagedResourcesAssembler<TResponseDto> assembler) {
        final var movies = this.getService().findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("{id}")
    @RequirePermissions(value = Permission.ADMIN)
    public ResponseEntity<TResponseDto> findById(@PathVariable @NotNull @Valid final TKey id) {
        final var result = this.getService().findById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping
    @RequirePermissions(value = Permission.ADMIN)
    public ResponseEntity<TResponseDto> create(@RequestBody @NotNull @Valid final TRequestDto requestDto) {
        final var responseDto = this.getService().create(requestDto);

        if (responseDto == null) {
            return ResponseEntity.internalServerError().build();
        }

        final var newEntityId = responseDto.id();
        final var location = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).findById(newEntityId))
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping("{id}")
    @RequirePermissions(value = Permission.ADMIN)
    public ResponseEntity<TResponseDto> update(@PathVariable @NotNull @Valid final TKey id,
            @RequestBody @Valid final TRequestDto requestDto) {
        final var result = this.getService().update(id, requestDto);

        final var responseDto = result.getValue();
        if (responseDto != null) {
            return ResponseEntity.ok(responseDto);
        }

        return switch (result.getError()) {
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @DeleteMapping("{id}")
    @RequirePermissions(value = Permission.ADMIN)
    public ResponseEntity<Void> deleteById(@PathVariable @NotNull @Valid final TKey id) {
        return switch (this.getService().deleteById(id)) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS_ERROR -> ResponseEntity.notFound().build();
        case UNSPECIFIED_ERROR -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    protected abstract AbstractEntityService<TRequestDto, TResponseDto, TEntity, TKey> getService();
}

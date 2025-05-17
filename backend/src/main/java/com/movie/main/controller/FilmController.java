package com.movie.main.controller;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.auth.RequirePermission;
import com.movie.main.config.OpenApiConfig;
import com.movie.main.dto.request.FilmRequestDto;
import com.movie.main.dto.response.FilmResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.entity.Film;
import com.movie.main.service.FilmService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/films")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class FilmController {
    @NotNull
    private final FilmService service;

    protected FilmController(@NotNull final FilmService service) {
        this.service = service;
    }

    @GetMapping
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<FilmResponseDto>>> findAllByDeletedFalse(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmResponseDto> assembler) {
        final var result = this.service.findAllByDeletedFalse(PageRequest.of(page, size))
                .map(FilmController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("deleted")
    public ResponseEntity<PagedModel<EntityModel<FilmResponseDto>>> findAllyDeletedTrue(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmResponseDto> assembler) {
        final var result = this.service.findAllByDeletedTrue(PageRequest.of(page, size))
                .map(FilmController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @PostMapping("search")
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<FilmResponseDto>>> searchAllFilmsWithTagsByDeletedFalse(
            @RequestParam @NotBlank final String keyword,
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmResponseDto> assembler) {
        var result = service.searchAllFilmsWithTagsByDeletedFalse(keyword, PageRequest.of(page, size))
                .map(FilmController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("{id}")
    @PermitAll
    public ResponseEntity<FilmResponseDto> findByIdAndDeletedFalse(@PathVariable final int id) {
        final var result = this.service.findByIdAndDeletedFalse(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(FilmController.getResponseDtoFrom(result));
    }

    @GetMapping("deleted/{id}")
    public ResponseEntity<FilmResponseDto> findByIdAndDeletedTrue(@PathVariable final int id) {
        final var result = this.service.findByIdAndDeletedTrue(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(FilmController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<FilmResponseDto> create(@RequestBody @Valid final FilmRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newFilm = result.getValue();

        if (newFilm != null) {
            final var responseDto = FilmController.getResponseDtoFrom(newFilm);
            final var location = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).findByIdAndDeletedFalse(responseDto.id()))
                    .toUri();

            return ResponseEntity.created(location).body(responseDto);
        }

        return switch (result.getError()) {
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PutMapping("{id}")
    public ResponseEntity<FilmResponseDto> updateById(@PathVariable final int id,
            @RequestBody @Valid final FilmRequestDto requestDto) {
        final var result = this.service.updateByIdAndDeletedFalse(id, requestDto);
        final var film = result.getValue();

        if (film != null) {
            return ResponseEntity.ok(FilmController.getResponseDtoFrom(film));
        }

        return switch (result.getError()) {
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("delete/{id}")
    public ResponseEntity<Void> markAsDeletedById(@PathVariable final int id) {
        return switch (this.service.markAsDeletedById(id)) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS_ERROR -> ResponseEntity.notFound().build();
        case UNSPECIFIED_ERROR -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("undelete/{id}")
    public ResponseEntity<Void> markAsUndeletedById(@PathVariable final int id) {
        return switch (this.service.markAsUndeletedById(id)) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS_ERROR -> ResponseEntity.notFound().build();
        case UNSPECIFIED_ERROR -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @NotNull
    public static FilmResponseDto getResponseDtoFrom(@NotNull final Film film) {
        return new FilmResponseDto(film.getId(), film.getName(), film.getThumbnailUrl(), film.getTrailerUrl(),
                film.getTags(), film.getDuration(), film.getAgeRestriction(), film.getVoice(),
                film.getOriginatedCountry(), film.is3D(), film.getDescription(), film.getContent(),
                film.getBeginDate());
    }
}

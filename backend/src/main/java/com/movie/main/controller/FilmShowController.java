package com.movie.main.controller;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.movie.main.dto.request.FilmShowRequestDto;
import com.movie.main.dto.response.FilmResponseDto;
import com.movie.main.dto.response.FilmShowResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.entity.FilmShow;
import com.movie.main.service.FilmShowService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/film-shows")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class FilmShowController {
    @NotNull
    private final FilmShowService service;

    protected FilmShowController(@NotNull final FilmShowService service) {
        this.service = service;
    }

    @GetMapping
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<FilmShowResponseDto>>> findAllByDeletedFalse(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmShowResponseDto> assembler) {
        final var result = this.service.findAllByDeletedFalse(PageRequest.of(page, size))
                .map(FilmShowController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("by-film/{date}")
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<FilmShowResponseDto>>> findAllByFilmIdOrderByDateTime(final int filmId,
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmShowResponseDto> assembler) {
        final var result = this.service.findAllByFilmIdOrderByDateTime(filmId, PageRequest.of(page, size))
                .map(FilmShowController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("by-date/{date}")
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<FilmShowResponseDto>>> findAllByShowDateAndDeletedFalse(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date,
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmShowResponseDto> assembler) {
        final var result = this.service.findAllByShowDateAndDeletedFalse(date, PageRequest.of(page, size))
                .map(FilmShowController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("by-film/{filmId}/by-date/{date}")
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<FilmShowResponseDto>>> findAllByFilmIdAndShowDateAndDeletedFalse(
            @PathVariable final int filmId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date,
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmShowResponseDto> assembler) {
        final var result = this.service
                .findAllByFilmIdAndShowDateAndDeletedFalse(filmId, date, PageRequest.of(page, size))
                .map(FilmShowController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("available-film-by-date/{date}")
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<FilmResponseDto>>> findAllFilmsByShowDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date,
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmResponseDto> assembler) {
        final var result = this.service.findAllFilmsByShowDateAndDeletedFalse(date, PageRequest.of(page, size))
                .map(FilmController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("showing")
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<FilmResponseDto>>> findAllFilmsShowingFromNowToEndOfToday(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmResponseDto> assembler) {
        final var result = this.service
                .findAllFilmsShowingFromNowToEndOfTodayAndDeletedFalse(PageRequest.of(page, size))
                .map(FilmController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("upcoming")
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<FilmResponseDto>>> findAllFilmsShowingFromTomorrow(
            @RequestParam(defaultValue = "7") @Min(value = 1) int days,
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmResponseDto> assembler) {
        final var result = this.service.findAllFilmsShowingFromTomorrowAndDeletedFalse(days, PageRequest.of(page, size))
                .map(FilmController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("deleted")
    public ResponseEntity<PagedModel<EntityModel<FilmShowResponseDto>>> findAllByDeletedTrue(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<FilmShowResponseDto> assembler) {
        final var movies = this.service.findAllByDeletedTrue(PageRequest.of(page, size))
                .map(FilmShowController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("{id}")
    @PermitAll
    public ResponseEntity<FilmShowResponseDto> findByIdAndDeletedFalse(@PathVariable final int id) {
        final var result = this.service.findByIdAndDeletedFalse(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(FilmShowController.getResponseDtoFrom(result));
    }

    @GetMapping("deleted/{id}")
    public ResponseEntity<FilmShowResponseDto> findByIdAndDeletedTrue(@PathVariable final int id) {
        final var result = this.service.findByIdAndDeletedTrue(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(FilmShowController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<FilmShowResponseDto> create(@RequestBody @Valid final FilmShowRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newFilmShow = result.getValue();

        if (newFilmShow != null) {
            final var responseDto = FilmShowController.getResponseDtoFrom(newFilmShow);
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
    public ResponseEntity<FilmShowResponseDto> updateById(@PathVariable final int id,
            @RequestBody @Valid final FilmShowRequestDto requestDto) {
        final var result = this.service.updateById(id, requestDto);
        final var filmShow = result.getValue();

        if (filmShow != null) {
            return ResponseEntity.ok(FilmShowController.getResponseDtoFrom(filmShow));
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
    public static FilmShowResponseDto getResponseDtoFrom(@NotNull final FilmShow filmShow) {
        return new FilmShowResponseDto(filmShow.getId(), filmShow.getFilm().getId(), filmShow.getRoom().getId(),
                filmShow.getShowDate(), filmShow.getShowTime(), filmShow.getType());
    }
}

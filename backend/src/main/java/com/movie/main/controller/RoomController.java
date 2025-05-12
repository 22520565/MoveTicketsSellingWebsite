package com.movie.main.controller;

import org.hibernate.validator.constraints.Range;
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
import com.movie.main.dto.request.RoomRequestDto;
import com.movie.main.dto.response.RoomResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.entity.Room;
import com.movie.main.service.RoomService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/rooms")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class RoomController {
    @NotNull
    private final RoomService service;

    protected RoomController(@NotNull final RoomService service) {
        this.service = service;
    }

    @GetMapping
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<RoomResponseDto>>> findAllByDeletedFalse(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<RoomResponseDto> assembler) {
        final var result = this.service.findAllByDeletedFalse(PageRequest.of(page, size))
                .map(RoomController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("deleted")
    public ResponseEntity<PagedModel<EntityModel<RoomResponseDto>>> findAllByDeletedTrue(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<RoomResponseDto> assembler) {
        final var movies = this.service.findAllByDeletedTrue(PageRequest.of(page, size))
                .map(RoomController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("{id}")
    @PermitAll
    public ResponseEntity<RoomResponseDto> findByIdAndDeletedFalse(@PathVariable final int id) {
        final var result = this.service.findByIdAndDeletedFalse(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(RoomController.getResponseDtoFrom(result));
    }

    @GetMapping("deleted/{id}")
    public ResponseEntity<RoomResponseDto> findByIdAndDeletedTrue(@PathVariable final int id) {
        final var result = this.service.findByIdAndDeletedTrue(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(RoomController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<RoomResponseDto> create(@RequestBody @Valid final RoomRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newRoom = result.getValue();

        if (newRoom != null) {
            final var responseDto = RoomController.getResponseDtoFrom(newRoom);
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
    public ResponseEntity<RoomResponseDto> updateById(@PathVariable final int id,
            @RequestBody @Valid final RoomRequestDto requestDto) {
        final var result = this.service.updateByIdAndDeletedFalse(id, requestDto);
        final var room = result.getValue();

        if (room != null) {
            return ResponseEntity.ok(RoomController.getResponseDtoFrom(room));
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
    public static RoomResponseDto getResponseDtoFrom(@NotNull final Room room) {
        return new RoomResponseDto(room.getId(), room.getName(), room.getNumberOfSeatRow(),
                room.getNumberOfSeatColumn(), room.getCenterX1(), room.getCenterX2(), room.getCenterY1(),
                room.getCenterY2(), room.getNote(), room.getTheater().getId());
    }
}

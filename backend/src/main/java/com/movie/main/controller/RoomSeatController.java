package com.movie.main.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.auth.RequirePermission;
import com.movie.main.config.OpenApiConfig;
import com.movie.main.dto.request.RoomSeatRequestDto;
import com.movie.main.dto.response.RoomSeatResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.entity.RoomSeat;
import com.movie.main.service.RoomSeatService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/room-seats")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class RoomSeatController {
    @NotNull
    private final RoomSeatService service;

    protected RoomSeatController(@NotNull final RoomSeatService service) {
        this.service = service;
    }

    @GetMapping
    @PermitAll
    public ResponseEntity<PagedModel<EntityModel<RoomSeatResponseDto>>> findAll(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<RoomSeatResponseDto> assembler) {
        final var movies = this.service.findAll(PageRequest.of(page, size)).map(RoomSeatController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("{id}")
    @PermitAll
    public ResponseEntity<RoomSeatResponseDto> findById(@PathVariable final int id) {
        final var result = this.service.findById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(RoomSeatController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<RoomSeatResponseDto> create(
            @RequestBody @NotNull @Valid final RoomSeatRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newRoomSeat = result.getValue();

        if (newRoomSeat != null) {
            final var responseDto = RoomSeatController.getResponseDtoFrom(newRoomSeat);
            final var location = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).findById(responseDto.id()))
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
    public ResponseEntity<RoomSeatResponseDto> updateById(@PathVariable final int id,
            @RequestBody @Valid final RoomSeatRequestDto requestDto) {
        final var result = this.service.updateById(id, requestDto);
        final var roomSeat = result.getValue();

        if (roomSeat != null) {
            return ResponseEntity.ok(RoomSeatController.getResponseDtoFrom(roomSeat));
        }

        return switch (result.getError()) {
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable final int id) {
        this.service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @NotNull
    public static RoomSeatResponseDto getResponseDtoFrom(@NotNull final RoomSeat roomSeat) {
        return new RoomSeatResponseDto(roomSeat.getId(), roomSeat.getName(), roomSeat.getType(),
                roomSeat.getRoom().getId());
    }
}

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
import com.movie.main.dto.request.EmployeeRequestDto;
import com.movie.main.dto.request.SetPasswordRequestDto;
import com.movie.main.dto.response.EmployeeResponseDto;
import com.movie.main.entity.Employee;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.exception.ConflictException;
import com.movie.main.service.EmployeeService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/employees")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class EmployeeController {
    @NotNull
    private final EmployeeService service;

    protected EmployeeController(@NotNull final EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<EmployeeResponseDto>>> findAllByBlockedFalseAndDeletedFalse(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<EmployeeResponseDto> assembler) {
        final var result = this.service.findAllByBlockedFalseAndDeletedFalse(PageRequest.of(page, size))
                .map(EmployeeController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("blocked")
    public ResponseEntity<PagedModel<EntityModel<EmployeeResponseDto>>> findAllByBlockedTrueAndDeletedFalse(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<EmployeeResponseDto> assembler) {
        final var result = this.service.findAllByBlockedTrueAndDeletedFalse(PageRequest.of(page, size))
                .map(EmployeeController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(result));
    }

    @GetMapping("deleted")
    public ResponseEntity<PagedModel<EntityModel<EmployeeResponseDto>>> findAllByDeletedTrue(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<EmployeeResponseDto> assembler) {
        final var movies = this.service.findAllByDeletedTrue(PageRequest.of(page, size))
                .map(EmployeeController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("{id}")
    public ResponseEntity<EmployeeResponseDto> findByIdAndBlockedFalseAndDeletedFalse(@PathVariable final int id) {
        final var result = this.service.findByIdAndBlockedFalseAndDeletedFalse(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(EmployeeController.getResponseDtoFrom(result));
    }

    @GetMapping("blocked/{id}")
    public ResponseEntity<EmployeeResponseDto> findByIdAndBlockedTrueAndDeletedFalse(@PathVariable final int id) {
        final var result = this.service.findByIdAndBlockedTrueAndDeletedFalse(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(EmployeeController.getResponseDtoFrom(result));
    }

    @GetMapping("deleted/{id}")
    public ResponseEntity<EmployeeResponseDto> findByIdAndDeletedTrue(@PathVariable final int id) {
        final var result = this.service.findByIdAndDeletedTrue(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(EmployeeController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> create(@RequestBody @Valid final EmployeeRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newEmployee = result.getValue();

        if (newEmployee != null) {
            final var responseDto = EmployeeController.getResponseDtoFrom(newEmployee);
            final var location = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                    .findByIdAndBlockedFalseAndDeletedFalse(responseDto.id())).toUri();

            return ResponseEntity.created(location).body(responseDto);
        }

        return switch (result.getError()) {
        case USERNAME_EXISTS -> throw new ConflictException("Username exists");
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PutMapping("{id}")
    public ResponseEntity<EmployeeResponseDto> updateByIdAndDeletedFalse(@PathVariable final int id,
            @RequestBody @Valid final EmployeeRequestDto requestDto) {
        final var result = this.service.updateByIdAndDeletedFalse(id, requestDto);
        final var employee = result.getValue();

        if (employee != null) {
            return ResponseEntity.ok(EmployeeController.getResponseDtoFrom(employee));
        }

        return switch (result.getError()) {
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case USERNAME_EXISTS -> throw new ConflictException("Username exists");
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("set-password/{id}")
    public ResponseEntity<Void> setPasswordByIdAndDeletedFalse(@PathVariable final int id,
            @RequestBody @Valid final SetPasswordRequestDto requestDto) {
        return switch (this.service.setPasswordByIdAndDeletedFalse(id, requestDto.password())) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS -> ResponseEntity.notFound().build();
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("block/{id}")
    public ResponseEntity<Void> markAsBlockedById(@PathVariable final int id) {
        return switch (this.service.markAsBlockedById(id)) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS_ERROR -> ResponseEntity.notFound().build();
        case UNSPECIFIED_ERROR -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PatchMapping("unblock/{id}")
    public ResponseEntity<Void> markAsUnblockedById(@PathVariable final int id) {
        return switch (this.service.markAsUnblockedById(id)) {
        case SUCCESS -> ResponseEntity.noContent().build();
        case ENTITY_NOT_EXISTS_ERROR -> ResponseEntity.notFound().build();
        case UNSPECIFIED_ERROR -> ResponseEntity.internalServerError().build();
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
    public static EmployeeResponseDto getResponseDtoFrom(@NotNull final Employee employee) {
        return new EmployeeResponseDto(employee.getId(), employee.getName(), employee.getBirthDate(),
                employee.getEmail(), employee.getPhoneNumber(), employee.getUsername(), employee.getJobTitle(),
                employee.getSalary(), employee.getShiftStart(), employee.getShiftEnd(), employee.getBeginWorkingDate(),
                employee.getPermissions());
    }
}

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
import com.movie.main.dto.request.EmployeeRequestDto;
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
    public ResponseEntity<PagedModel<EntityModel<EmployeeResponseDto>>> findAllByDeletedFalse(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<EmployeeResponseDto> assembler) {
        final var movies = this.service.findAllByDeletedFalse(PageRequest.of(page, size))
                .map(EmployeeController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("all")
    public ResponseEntity<PagedModel<EntityModel<EmployeeResponseDto>>> findAll(
            @RequestParam(defaultValue = ControllerConfig.PAGE_NUMBER_STRING) @Min(value = 0) final int page,
            @RequestParam(defaultValue = ControllerConfig.PAGE_SIZE_STRING) @Range(min = 1, max = ControllerConfig.MAX_PAGE_SIZE) final int size,
            final PagedResourcesAssembler<EmployeeResponseDto> assembler) {
        final var movies = this.service.findAll(PageRequest.of(page, size)).map(EmployeeController::getResponseDtoFrom);
        return ResponseEntity.ok(assembler.toModel(movies));
    }

    @GetMapping("{id}")
    public ResponseEntity<EmployeeResponseDto> findByIdAndDeletedFalse(@PathVariable final int id) {
        final var result = this.service.findByIdAndDeletedFalse(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(EmployeeController.getResponseDtoFrom(result));
    }

    @GetMapping("all/{id}")
    public ResponseEntity<EmployeeResponseDto> findById(@PathVariable final int id) {
        final var result = this.service.findById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(EmployeeController.getResponseDtoFrom(result));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> create(
            @RequestBody @NotNull @Valid final EmployeeRequestDto requestDto) {
        final var result = this.service.create(requestDto);
        final var newEmployee = result.getValue();

        if (newEmployee != null) {
            final var responseDto = EmployeeController.getResponseDtoFrom(newEmployee);
            final var location = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).findByIdAndDeletedFalse(responseDto.id()))
                    .toUri();

            return ResponseEntity.created(location).body(responseDto);
        }

        return switch (result.getError()) {
        case USERNAME_EXISTS -> throw new ConflictException("Username exists");
        case UNSPECIFIED -> ResponseEntity.internalServerError().build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PutMapping("{id}")
    public ResponseEntity<EmployeeResponseDto> updateById(@PathVariable final int id,
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

    @DeleteMapping("{id}")
    public ResponseEntity<Void> markAsDeletedById(@PathVariable final int id) {
        return switch (this.service.markAsDeletedById(id)) {
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

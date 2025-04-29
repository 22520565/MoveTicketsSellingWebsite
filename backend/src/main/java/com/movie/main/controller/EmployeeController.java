package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.EmployeeRequestDto;
import com.movie.main.dto.response.EmployeeResponseDto;
import com.movie.main.entity.Employee;
import com.movie.main.service.EmployeeService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController
        extends AbstractEntityController<EmployeeRequestDto, EmployeeResponseDto, Employee, Integer> {
    @NotNull
    private final EmployeeService service;

    protected EmployeeController(@NotNull final EmployeeService service) {
        this.service = service;
    }

    @Override
    protected final EmployeeService getService() {
        return this.service;
    }
}

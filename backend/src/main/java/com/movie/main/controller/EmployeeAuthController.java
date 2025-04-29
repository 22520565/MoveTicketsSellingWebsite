package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.EmployeeRequestDto;
import com.movie.main.dto.response.EmployeeResponseDto;
import com.movie.main.entity.Employee;
import com.movie.main.service.EmployeeAuthService;
import com.movie.main.service.EmployeeService;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/auth/employee")
public class EmployeeAuthController extends
        AbstractUserAuthController<EmployeeAuthService, EmployeeService, EmployeeRequestDto, EmployeeResponseDto, Employee> {
    @NotNull
    private final EmployeeAuthService employeeAuthService;

    public EmployeeAuthController(@NotNull final EmployeeAuthService employeeAuthService) {
        this.employeeAuthService = employeeAuthService;
    }

    @Override
    protected EmployeeAuthService getUserAuthService() {
        return this.employeeAuthService;
    }

}

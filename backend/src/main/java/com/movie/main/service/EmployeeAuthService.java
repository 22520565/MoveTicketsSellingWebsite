package com.movie.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.EmployeeRequestDto;
import com.movie.main.dto.response.EmployeeResponseDto;
import com.movie.main.entity.Employee;
import com.movie.main.entity.AbstractUserDetail.UserRole;

import jakarta.validation.constraints.NotNull;

@Service
public class EmployeeAuthService
        extends AbstractUserAuthService<EmployeeService, EmployeeRequestDto, EmployeeResponseDto, Employee> {
    @NotNull
    private final EmployeeService employeeService;

    @NotNull
    private final UserRefreshTokenService userRefreshTokenService;

    @NotNull
    private final PasswordEncoder passwordEncoder;

    public EmployeeAuthService(@NotNull final EmployeeService employeeService,
            @NotNull final UserRefreshTokenService userRefreshTokenService,
            @NotNull final PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.userRefreshTokenService = userRefreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserRole getUserRole() {
        return Employee.userRole;
    }

    @Override
    protected EmployeeService getUserDetailsService() {
        return this.employeeService;
    }

    @Override
    protected UserRefreshTokenService getUserRefreshTokenService() {
        return this.userRefreshTokenService;
    }

    @Override
    protected PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }
}

package com.movie.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.main.config.JwtTokenProvider;
import com.movie.main.dto.request.EmployeeRequestDto;
import com.movie.main.dto.response.EmployeeResponseDto;
import com.movie.main.entity.Employee;

import jakarta.validation.constraints.NotNull;

@Service
public class EmployeeAuthService
        extends AbstractUserAuthService<EmployeeService, EmployeeRequestDto, EmployeeResponseDto, Employee> {
    @NotNull
    private final EmployeeService employeeService;

    @NotNull
    private final PasswordEncoder passwordEncoder;

    @NotNull
    private final JwtTokenProvider jwtTokenProvider;

    public EmployeeAuthService(
            @NotNull final EmployeeService employeeService,
            @NotNull final PasswordEncoder passwordEncoder,
            @NotNull final JwtTokenProvider jwtTokenProvider) {
        this.employeeService = employeeService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected EmployeeService getUserDetailsService() {
        return this.employeeService;
    }

    @Override
    protected JwtTokenProvider getJwtTokenProvider() {
        return this.jwtTokenProvider;
    }

    @Override
    protected PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

}

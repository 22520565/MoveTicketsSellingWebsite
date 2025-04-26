package com.movie.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.main.auth.JwtTokenProvider;
import com.movie.main.dto.request.CustomerRequestDto;
import com.movie.main.dto.response.CustomerResponseDto;
import com.movie.main.entity.Customer;
import com.movie.main.entity.UserDetailsInterface.UserRole;

import jakarta.validation.constraints.NotNull;

@Service
public class CustomerAuthService
        extends AbstractUserAuthService<CustomerService, CustomerRequestDto, CustomerResponseDto, Customer> {
    @NotNull
    private final CustomerService customerService;

    @NotNull
    private final PasswordEncoder passwordEncoder;

    @NotNull
    private final JwtTokenProvider jwtTokenProvider;

    public CustomerAuthService(@NotNull final CustomerService customerService,
            @NotNull final PasswordEncoder passwordEncoder, @NotNull final JwtTokenProvider jwtTokenProvider) {
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected CustomerService getUserDetailsService() {
        return this.customerService;
    }

    @Override
    protected JwtTokenProvider getJwtTokenProvider() {
        return this.jwtTokenProvider;
    }

    @Override
    protected PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    @Override
    protected UserRole getUserRole() {
        return Customer.userRole;
    }
}

package com.movie.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.main.auth.JwtTokenProvider;
import com.movie.main.dto.request.CustomerRequestDto;
import com.movie.main.dto.response.CustomerResponseDto;
import com.movie.main.entity.Customer;
import com.movie.main.entity.AbstractUserDetail.UserRole;

import jakarta.validation.constraints.NotNull;

@Service
public class CustomerAuthService
        extends AbstractUserAuthService<CustomerService, CustomerRequestDto, CustomerResponseDto, Customer> {
    @NotNull
    private final CustomerService customerService;

    @NotNull
    private final UserRefreshTokenService userRefreshTokenService;

    @NotNull
    private final PasswordEncoder passwordEncoder;

    public CustomerAuthService(@NotNull final CustomerService customerService,
            @NotNull final UserRefreshTokenService userRefreshTokenService,
            @NotNull final PasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.userRefreshTokenService = userRefreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserRole getUserRole() {
        return Customer.userRole;
    }

    @Override
    protected CustomerService getUserDetailsService() {
        return this.customerService;
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

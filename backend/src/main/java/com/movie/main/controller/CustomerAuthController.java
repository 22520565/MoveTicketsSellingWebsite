package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.CustomerRequestDto;
import com.movie.main.dto.response.CustomerResponseDto;
import com.movie.main.entity.Customer;
import com.movie.main.service.CustomerAuthService;
import com.movie.main.service.CustomerService;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/auth/customer")
public class CustomerAuthController extends
        AbstractUserAuthController<CustomerAuthService, CustomerService, CustomerRequestDto, CustomerResponseDto, Customer> {
    @NotNull
    private final CustomerAuthService customerAuthService;

    public CustomerAuthController(@NotNull final CustomerAuthService customerAuthService) {
        this.customerAuthService = customerAuthService;
    }

    @Override
    protected CustomerAuthService getUserAuthService() {
        return this.customerAuthService;
    }

}

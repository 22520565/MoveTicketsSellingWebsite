package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.CustomerRequestDto;
import com.movie.main.dto.response.CustomerResponseDto;
import com.movie.main.entity.Customer;
import com.movie.main.service.CustomerService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/customers")
public class CustomerController
        extends AbstractEntityController<CustomerRequestDto, CustomerResponseDto, Customer, Integer> {
    @NotNull
    private final CustomerService service;

    protected CustomerController(@NotNull final CustomerService service) {
        this.service = service;
    }

    @Override
    protected final CustomerService getService() {
        return this.service;
    }
}

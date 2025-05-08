package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.CustomerRequestDto;
import com.movie.main.dto.response.CustomerResponseDto;
import com.movie.main.entity.Customer;
import com.movie.main.repository.CustomerRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class CustomerService extends AbstractUserDetailsService<CustomerRequestDto, CustomerResponseDto, Customer> {
    @NotNull
    private final CustomerRepository repository;

    @NotNull
    private final UserService userService;

    public CustomerService(@NotNull final CustomerRepository repository, @NotNull final UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public CustomerResponseDto createResponseDtoFromEntity(@NotNull final Customer customer) {
        return new CustomerResponseDto(this.userService.createResponseDtoFromEntity(customer.getUser()));
    }

    @Override
    protected Customer createEntityFromRequestDto(@NotNull final CustomerRequestDto requestDto) {
        final var user = this.userService.createEntityFromRequestDto(requestDto.userRequestDto());
        if (user == null) {
            return null;
        }

        return new Customer(user);
    }

    @Override
    protected Customer updateEntityFromRequestDto(@NotNull final Customer customer,
            @NotNull final CustomerRequestDto requestDto) {
        final var user = this.userService.updateEntityFromRequestDto(customer.getUser(), requestDto.userRequestDto());
        if (user == null) {
            return null;
        }

        customer.setUser(user);

        return customer;
    }

    @Override
    public CustomerRepository getRepository() {
        return this.repository;
    }
}

package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.CustomerOrderRequestDto;
import com.movie.main.entity.CustomerOrder;
import com.movie.main.repository.CustomerOrderRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerOrderService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    @NotNull
    private final CustomerOrderRepository repository;

    @NotNull
    private final CustomerService customerService;

    public CustomerOrderService(@NotNull final CustomerOrderRepository repository,
            @NotNull final CustomerService customerService) {
        this.repository = repository;
        this.customerService = customerService;
    }

    @NotNull
    public Page<@NotNull CustomerOrder> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public CustomerOrder findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<CustomerOrder, CreationError> create(@NotNull final CustomerOrderRequestDto requestDto) {
        final var customer = this.customerService.findByIdAndBlockedFalseAndDeletedFalse(requestDto.customerId());
        if (customer == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var newCustomerOrder = new CustomerOrder(requestDto.date(), requestDto.verifyCode(),
                requestDto.totalPrice(), requestDto.totalPriceAfterDiscount(), customer);

        try {
            return Expected.success(this.repository.save(newCustomerOrder));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<CustomerOrder, UpdateError> updateById(final int id,
            @NotNull final CustomerOrderRequestDto requestDto) {
        final var customer = this.customerService.findByIdAndBlockedFalseAndDeletedFalse(requestDto.customerId());
        if (customer == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var customerOrder = this.findById(id);
        if (customerOrder == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        customerOrder.setDate(requestDto.date());
        customerOrder.setVerifyCode(requestDto.verifyCode());
        customerOrder.setTotalPrice(requestDto.totalPrice());
        customerOrder.setTotalPriceAfterDiscount(requestDto.totalPriceAfterDiscount());
        customerOrder.setCustomer(customer);

        try {
            return Expected.success(this.repository.save(customerOrder));
        }
        catch (final Exception exception) {
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public void deleteById(final int id) {
        this.repository.deleteById(id);
    }
}

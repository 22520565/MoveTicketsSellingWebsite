package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.OrderDecoratorsPointUsageRequestDto;
import com.movie.main.entity.OrderDecoratorsPointUsage;
import com.movie.main.repository.OrderDecoratorsPointUsageRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderDecoratorsPointUsageService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    @NotNull
    private final OrderDecoratorsPointUsageRepository repository;

    @NotNull
    private final CustomerOrderService customerOrderService;

    public OrderDecoratorsPointUsageService(@NotNull final OrderDecoratorsPointUsageRepository repository,
            @NotNull final CustomerOrderService customerOrderService) {
        this.repository = repository;
        this.customerOrderService = customerOrderService;
    }

    @NotNull
    public Page<@NotNull OrderDecoratorsPointUsage> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public OrderDecoratorsPointUsage findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<OrderDecoratorsPointUsage, CreationError> create(
            @NotNull final OrderDecoratorsPointUsageRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var newOrderDecoratorsPointUsage = new OrderDecoratorsPointUsage(customerOrder, requestDto.pointUsed(),
                requestDto.pointToMoneyRatio());

        try {
            return Expected.success(this.repository.save(newOrderDecoratorsPointUsage));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<OrderDecoratorsPointUsage, UpdateError> updateById(final int id,
            @NotNull final OrderDecoratorsPointUsageRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var orderDecoratorsPointUsage = this.findById(id);
        if (orderDecoratorsPointUsage == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        orderDecoratorsPointUsage.setCustomerOrder(customerOrder);
        orderDecoratorsPointUsage.setPointUsed(requestDto.pointUsed());
        orderDecoratorsPointUsage.setPointToMoneyRatio(requestDto.pointToMoneyRatio());

        try {
            return Expected.success(this.repository.save(orderDecoratorsPointUsage));
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

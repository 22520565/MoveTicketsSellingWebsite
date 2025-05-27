package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.OrderDecoratorsOfflineServiceRequestDto;
import com.movie.main.entity.OrderDecoratorsOfflineService;
import com.movie.main.repository.OrderDecoratorsOfflineServiceRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderDecoratorsOfflineServiceService {
    public enum CreationError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    @NotNull
    private final OrderDecoratorsOfflineServiceRepository repository;

    @NotNull
    private final CustomerOrderService customerOrderService;

    public OrderDecoratorsOfflineServiceService(@NotNull final OrderDecoratorsOfflineServiceRepository repository,
            @NotNull final CustomerOrderService customerOrderService) {
        this.repository = repository;
        this.customerOrderService = customerOrderService;
    }

    @NotNull
    public Page<@NotNull OrderDecoratorsOfflineService> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public OrderDecoratorsOfflineService findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<OrderDecoratorsOfflineService, CreationError> create(
            @NotNull final OrderDecoratorsOfflineServiceRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var newOrderDecoratorsOfflineService = new OrderDecoratorsOfflineService(
                customerOrder,
                requestDto.printed(),
                requestDto.served(),
                requestDto.invalidReasonPrinted(),
                requestDto.invalidReasonServed());

        try {
            return Expected.success(this.repository.save(newOrderDecoratorsOfflineService));
        } catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<OrderDecoratorsOfflineService, UpdateError> updateById(final int id,
            @NotNull final OrderDecoratorsOfflineServiceRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var orderDecoratorsOfflineService = this.findById(id);
        if (orderDecoratorsOfflineService == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        orderDecoratorsOfflineService.setCustomerOrder(customerOrder);
        orderDecoratorsOfflineService.setPrinted(requestDto.printed());
        orderDecoratorsOfflineService.setServed(requestDto.served());
        orderDecoratorsOfflineService.setInvalidReasonPrinted(requestDto.invalidReasonPrinted());
        orderDecoratorsOfflineService.setInvalidReasonServed(requestDto.invalidReasonServed());

        try {
            return Expected.success(this.repository.save(orderDecoratorsOfflineService));
        } catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public void deleteById(final int id) {
        this.repository.deleteById(id);
    }
}

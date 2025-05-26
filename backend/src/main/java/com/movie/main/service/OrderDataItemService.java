package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.OrderDataItemRequestDto;
import com.movie.main.entity.OrderDataItem;
import com.movie.main.repository.OrderDataItemRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderDataItemService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    @NotNull
    private final OrderDataItemRepository repository;

    @NotNull
    private final CustomerOrderService customerOrderService;

    public OrderDataItemService(@NotNull final OrderDataItemRepository repository,
            @NotNull final CustomerOrderService customerOrderService) {
        this.repository = repository;
        this.customerOrderService = customerOrderService;
    }

    @NotNull
    public Page<@NotNull OrderDataItem> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public OrderDataItem findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<OrderDataItem, CreationError> create(@NotNull final OrderDataItemRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var newOrderDataItem = new OrderDataItem(customerOrder, requestDto.items());

        try {
            return Expected.success(this.repository.save(newOrderDataItem));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<OrderDataItem, UpdateError> updateById(final int id,
            @NotNull final OrderDataItemRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var orderDataItem = this.findById(id);
        if (orderDataItem == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        orderDataItem.setCustomerOrder(customerOrder);
        orderDataItem.setItems(requestDto.items());

        try {
            return Expected.success(this.repository.save(orderDataItem));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public void deleteById(final int id) {
        this.repository.deleteById(id);
    }
}

package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.OrderItemRequestDto;
import com.movie.main.entity.OrderItem;
import com.movie.main.repository.OrderItemRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderItemService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    @NotNull
    private final OrderItemRepository repository;

    public OrderItemService(@NotNull final OrderItemRepository repository) {
        this.repository = repository;
    }

    @NotNull
    public Page<@NotNull OrderItem> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public OrderItem findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<OrderItem, CreationError> create(@NotNull final OrderItemRequestDto requestDto) {
        final var newOrderItem = new OrderItem(requestDto.name(), requestDto.quantity(), requestDto.price());

        try {
            return Expected.success(this.repository.save(newOrderItem));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<OrderItem, UpdateError> updateById(final int id, @NotNull final OrderItemRequestDto requestDto) {
        final var orderItem = this.findById(id);
        if (orderItem == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        orderItem.setName(requestDto.name());
        orderItem.setQuantity(requestDto.quantity());
        orderItem.setPrice(requestDto.price());

        try {
            return Expected.success(this.repository.save(orderItem));
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

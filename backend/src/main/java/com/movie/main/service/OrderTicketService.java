package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.OrderTicketRequestDto;
import com.movie.main.entity.OrderTicket;
import com.movie.main.repository.OrderTicketRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderTicketService {
    public enum CreationError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    @NotNull
    private final OrderTicketRepository repository;

    public OrderTicketService(@NotNull final OrderTicketRepository repository) {
        this.repository = repository;
    }

    @NotNull
    public Page<@NotNull OrderTicket> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public OrderTicket findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<OrderTicket, CreationError> create(@NotNull final OrderTicketRequestDto requestDto) {
        final var newOrderTicket = new OrderTicket(
                requestDto.name(),
                requestDto.quantity(),
                requestDto.price());

        try {
            return Expected.success(this.repository.save(newOrderTicket));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<OrderTicket, UpdateError> updateById(
            final int id,
            @NotNull final OrderTicketRequestDto requestDto) {
        final var orderTicket = this.findById(id);
        if (orderTicket == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        orderTicket.setName(requestDto.name());
        orderTicket.setQuantity(requestDto.quantity());
        orderTicket.setPrice(requestDto.price());

        try {
            return Expected.success(this.repository.save(orderTicket));
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

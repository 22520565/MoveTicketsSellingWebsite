package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.OrderDataFilmRequestDto;
import com.movie.main.entity.OrderDataFilm;
import com.movie.main.repository.OrderDataFilmRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderDataFilmService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    @NotNull
    private final OrderDataFilmRepository repository;

    @NotNull
    private final CustomerOrderService customerOrderService;

    public OrderDataFilmService(@NotNull final OrderDataFilmRepository repository,
            @NotNull final CustomerOrderService customerOrderService) {
        this.repository = repository;
        this.customerOrderService = customerOrderService;
    }

    @NotNull
    public Page<@NotNull OrderDataFilm> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public OrderDataFilm findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Nullable
    public OrderDataFilm findByIdAndCustomerId(final int id, final int customerId) {
        return this.repository.findByIdAndCustomerId(id, customerId).orElse(null);
    }

    @NotNull
    public Expected<OrderDataFilm, CreationError> create(@NotNull final OrderDataFilmRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var newOrderDataFilm = new OrderDataFilm(customerOrder, requestDto.filmName(),
                requestDto.ageRestriction(), requestDto.date(), requestDto.time(), requestDto.verifyCode(),
                requestDto.roomName(), requestDto.seatNames(), requestDto.tickets());

        try {
            return Expected.success(this.repository.save(newOrderDataFilm));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<OrderDataFilm, UpdateError> updateById(final int id,
            @NotNull final OrderDataFilmRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var orderDataFilm = this.findById(id);
        if (orderDataFilm == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        orderDataFilm.setCustomerOrder(customerOrder);
        orderDataFilm.setFilmName(requestDto.filmName());
        orderDataFilm.setAgeRestriction(requestDto.ageRestriction());
        orderDataFilm.setDate(requestDto.date());
        orderDataFilm.setTime(requestDto.time());
        orderDataFilm.setVerifyCode(requestDto.verifyCode());
        orderDataFilm.setRoomName(requestDto.roomName());
        orderDataFilm.setSeatNames(requestDto.seatNames());
        orderDataFilm.setTickets(requestDto.tickets());

        try {
            return Expected.success(this.repository.save(orderDataFilm));
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

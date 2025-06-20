package com.movie.main.service;

import java.util.HashSet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.OrderDataFilmRequestDto;
import com.movie.main.entity.OrderDataFilm;
import com.movie.main.entity.OrderTicket;
import com.movie.main.entity.RoomSeat;
import com.movie.main.repository.OrderDataFilmRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderDataFilmService {
    public enum CreationError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    @NotNull
    private final OrderDataFilmRepository repository;

    @NotNull
    private final CustomerOrderService customerOrderService;

    @NotNull
    private final FilmShowService filmShowService;

    @NotNull
    private final RoomSeatService roomSeatService;

    @NotNull
    private final OrderTicketService orderTicketService;

    public OrderDataFilmService(@NotNull final OrderDataFilmRepository repository,
            @NotNull final CustomerOrderService customerOrderService,
            @NotNull final FilmShowService filmShowService,
            @NotNull final RoomSeatService roomSeatService,
            @NotNull final OrderTicketService orderTicketService) {
        this.repository = repository;
        this.customerOrderService = customerOrderService;
        this.filmShowService = filmShowService;
        this.roomSeatService = roomSeatService;
        this.orderTicketService = orderTicketService;
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
    public OrderDataFilm findByIdAndCustomerId(
            final int id,
            final int customerId) {
        return this.repository.findByIdAndCustomerId(id, customerId).orElse(null);
    }

    @NotNull
    public Expected<OrderDataFilm, CreationError> create(@NotNull final OrderDataFilmRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var filmShow = this.filmShowService.findByIdAndDeletedFalse(requestDto.filmShowId());
        if (filmShow == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var roomSeatIds = requestDto.roomSeatIds();
        final HashSet<RoomSeat> roomSeats = HashSet.newHashSet(roomSeatIds.size());
        for (final var roomSeatId : roomSeatIds) {
            final var roomSeat = this.roomSeatService.findById(roomSeatId);

            if (roomSeat == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            roomSeats.add(roomSeat);
        }

        final var orderTicketIds = requestDto.orderTicketIds();
        final HashSet<OrderTicket> orderTickets = HashSet.newHashSet(orderTicketIds.size());
        for (final var orderTicketId : orderTicketIds) {
            final var orderTicket = this.orderTicketService.findById(orderTicketId);

            if (orderTicket == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            orderTickets.add(orderTicket);
        }

        final var newOrderDataFilm = new OrderDataFilm(
                customerOrder,
                requestDto.date(),
                requestDto.time(),
                filmShow,
                requestDto.verifyCode(),
                roomSeats,
                orderTickets);

        try {
            return Expected.success(this.repository.save(newOrderDataFilm));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<OrderDataFilm, UpdateError> updateById(
            final int id,
            @NotNull final OrderDataFilmRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var orderDataFilm = this.findById(id);
        if (orderDataFilm == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var filmShow = this.filmShowService.findByIdAndDeletedFalse(requestDto.filmShowId());
        if (filmShow == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var roomSeatIds = requestDto.roomSeatIds();
        final HashSet<RoomSeat> roomSeats = HashSet.newHashSet(roomSeatIds.size());
        for (final var roomSeatId : roomSeatIds) {
            final var roomSeat = this.roomSeatService.findById(roomSeatId);

            if (roomSeat == null) {
                return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
            }

            roomSeats.add(roomSeat);
        }

        final var orderTicketIds = requestDto.orderTicketIds();
        final HashSet<OrderTicket> orderTickets = HashSet.newHashSet(orderTicketIds.size());
        for (final var orderTicketId : orderTicketIds) {
            final var orderTicket = this.orderTicketService.findById(orderTicketId);

            if (orderTicket == null) {
                return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
            }

            orderTickets.add(orderTicket);
        }

        orderDataFilm.setCustomerOrder(customerOrder);
        orderDataFilm.setDate(requestDto.date());
        orderDataFilm.setTime(requestDto.time());
        orderDataFilm.setFilmShow(filmShow);
        orderDataFilm.setVerifyCode(requestDto.verifyCode());
        orderDataFilm.setSeatNames(roomSeats);
        orderDataFilm.setOrderTickets(orderTickets);

        try {
            return Expected.success(this.repository.save(orderDataFilm));
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

package com.movie.main.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.movie.main.controller.OrderTicketController;
import com.movie.main.dto.request.CreateOrderRequestDto;
import com.movie.main.entity.AdditionalItem;
import com.movie.main.entity.Customer;
import com.movie.main.entity.CustomerOrder;
import com.movie.main.entity.OrderDataFilm;
import com.movie.main.entity.OrderDataItem;
import com.movie.main.entity.OrderDecoratorsOfflineService;
import com.movie.main.entity.OrderDecoratorsPointUsage;
import com.movie.main.entity.OrderDecoratorsPromotion;
import com.movie.main.entity.OrderItem;
import com.movie.main.entity.OrderTicket;
import com.movie.main.entity.Promotion;
import com.movie.main.entity.RoomSeat;
import com.movie.main.repository.AdditionalItemRepository;
import com.movie.main.repository.CustomerOrderRepository;
import com.movie.main.repository.CustomerRepository;
import com.movie.main.repository.FilmShowRepository;
import com.movie.main.repository.OrderDataFilmRepository;
import com.movie.main.repository.OrderDataItemRepository;
import com.movie.main.repository.OrderDecoratorsOfflineServiceRepository;
import com.movie.main.repository.OrderDecoratorsPointUsageRepository;
import com.movie.main.repository.OrderDecoratorsPromotionRepository;
import com.movie.main.repository.PromotionRepository;
import com.movie.main.repository.RoomSeatRepository;
import com.movie.main.repository.TicketTypeRepository;
import com.movie.main.ulti.Expected;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MainOrderService {

    private final OrderTicketController orderTicketController;

    public enum CreationError {
        ENTITY_NOT_EXISTS,
        INSUFFICIENT_LOYAL_POINT,
        UNSPECIFIED,
    }

    @NotNull
    private final CustomerRepository customerRepository;

    @NotNull
    private final FilmShowRepository filmShowRepository;

    @NotNull
    private final TicketTypeRepository ticketTypeRepository;

    @NotNull
    private final RoomSeatRepository roomSeatRepository;

    @NotNull
    private final AdditionalItemRepository additionalItemRepository;

    @NotNull
    private final PromotionRepository promotionRepository;

    @NotNull
    private final ParamService paramService;

    @NotNull
    private final CustomerOrderRepository customerOrderRepository;

    @NotNull
    private final OrderDataFilmRepository orderDataFilmRepository;

    @NotNull
    private final OrderDataItemRepository orderDataItemRepository;

    @NotNull
    private final OrderDecoratorsOfflineServiceRepository orderDecoratorsOfflineServiceRepository;

    @NotNull
    private final OrderDecoratorsPointUsageRepository orderDecoratorsPointUsageRepository;

    @NotNull
    private final OrderDecoratorsPromotionRepository orderDecoratorsPromotionRepository;

    public MainOrderService(
            @NotNull final CustomerRepository customerRepository,
            @NotNull final FilmShowRepository filmShowRepository,
            @NotNull final TicketTypeRepository ticketTypeRepository,
            @NotNull final OrderTicketController orderTicketController,
            @NotNull final RoomSeatRepository roomSeatRepository,
            @NotNull final AdditionalItemRepository additionalItemRepository,
            @NotNull final PromotionRepository promotionRepository,
            @NotNull final ParamService paramService,
            @NotNull final CustomerOrderRepository customerOrderRepository,
            @NotNull final OrderDataFilmRepository orderDataFilmRepository,
            @NotNull final OrderDataItemRepository orderDataItemRepository,
            @NotNull final OrderDecoratorsOfflineServiceRepository orderDecoratorsOfflineServiceRepository,
            @NotNull final OrderDecoratorsPointUsageRepository orderDecoratorsPointUsageRepository,
            @NotNull final OrderDecoratorsPromotionRepository orderDecoratorsPromotionRepository) {
        this.customerRepository = customerRepository;
        this.filmShowRepository = filmShowRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.orderTicketController = orderTicketController;
        this.roomSeatRepository = roomSeatRepository;
        this.additionalItemRepository = additionalItemRepository;
        this.promotionRepository = promotionRepository;
        this.paramService = paramService;
        this.customerOrderRepository = customerOrderRepository;
        this.orderDataFilmRepository = orderDataFilmRepository;
        this.orderDataItemRepository = orderDataItemRepository;
        this.orderDecoratorsOfflineServiceRepository = orderDecoratorsOfflineServiceRepository;
        this.orderDecoratorsPointUsageRepository = orderDecoratorsPointUsageRepository;
        this.orderDecoratorsPromotionRepository = orderDecoratorsPromotionRepository;
    }

    @Transactional
    public Expected<CustomerOrder, CreationError> createOrder(
            @NotNull final CreateOrderRequestDto requestDto,
            final int customerId) {
        final var param = this.paramService.getParam();
        if (param == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var customer = this.customerRepository.findByIdAndBlockedFalseAndDeletedFalse(customerId).orElse(null);
        if (customer == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var customerLoyalPoint = customer.getLoyalPoint();
        final var pointUsage = requestDto.pointUsage();
        if (customerLoyalPoint < pointUsage) {
            return Expected.failure(CreationError.INSUFFICIENT_LOYAL_POINT);
        }
        customer.setLoyalPoint(customerLoyalPoint - pointUsage);

        final var filmShow = this.filmShowRepository.findByIdAndDeletedFalse(requestDto.filmShowId()).orElse(null);
        if (filmShow == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var tickets = requestDto.tickets();
        final Set<OrderTicket> orderTickets = HashSet.newHashSet(tickets.size());
        for (final var ticket : tickets) {
            final var ticketType = this.ticketTypeRepository.findById(ticket.typeId()).orElse(null);

            if (ticketType == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            orderTickets.add(new OrderTicket(ticketType.getTitle(), ticket.quantity(), ticketType.getPrice()));
        }

        final var seatIds = requestDto.seatIds();
        final Set<RoomSeat> roomSeats = HashSet.newHashSet(seatIds.size());
        for (final var seatId : seatIds) {
            final var roomSeat = this.roomSeatRepository.findById(seatId).orElse(null);

            if (roomSeat == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            roomSeats.add(roomSeat);
        }

        final var items = requestDto.items();
        final Set<OrderItem> orderItems = HashSet.newHashSet(items.size());
        for (final var item : items) {
            final var additionalItem = this.additionalItemRepository.findByIdAndDeletedFalse(item.id()).orElse(null);

            if (additionalItem == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            orderItems.add(new OrderItem(additionalItem.getName(), item.quantity(), additionalItem.getPrice()));
        }

        final var promotionIds = requestDto.promotionIds();
        final Set<Promotion> promotions = HashSet.newHashSet(promotionIds.size());
        for (final var promotionId : promotionIds) {
            final var promotion = this.promotionRepository.findById(promotionId).orElse(null);

            if (promotion == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            promotions.add(promotion);
        }

        try {
            final var customerOrder = this.customerOrderRepository.save(new CustomerOrder(
                    LocalDate.now(),
                    UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.getDefault()),
                    requestDto.totalPrice(),
                    requestDto.totalPriceAfterDiscount(),
                    customer));

            this.orderDataFilmRepository.save(new OrderDataFilm(
                    customerOrder,
                    LocalDate.now(),
                    LocalTime.now(),
                    filmShow,
                    roomSeats,
                    orderTickets));

            this.orderDataItemRepository.save(new OrderDataItem(
                    customerOrder,
                    orderItems));

            this.orderDecoratorsOfflineServiceRepository.save(new OrderDecoratorsOfflineService(
                    customerOrder,
                    false,
                    false,
                    null,
                    null));

            this.orderDecoratorsPointUsageRepository.save(new OrderDecoratorsPointUsage(
                    customerOrder,
                    pointUsage,
                    param.getLoyalPointPointToReducedPriceRatio()));

            this.orderDecoratorsPromotionRepository.save(new OrderDecoratorsPromotion(
                    customerOrder,
                    promotions));

            return Expected.success(customerOrder);
        }
        catch (final Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }
}

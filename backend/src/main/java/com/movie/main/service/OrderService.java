package com.movie.main.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.main.auth.RequirePermissionAspect;
import com.movie.main.dto.request.OrderRequestDto;
import com.movie.main.dto.response.OrderResponseDto;
import com.movie.main.dto.response.OrderResponseDto.ItemResponseDto;
import com.movie.main.dto.response.OrderResponseDto.SeatResponseDto;
import com.movie.main.dto.response.OrderResponseDto.TicketResponseDto;
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
import com.movie.main.entity.StripePayment;
import com.movie.main.event.OrderCreatedEvent;
import com.movie.main.repository.AdditionalItemRepository;
import com.movie.main.repository.CustomerOrderRepository;
import com.movie.main.repository.CustomerRepository;
import com.movie.main.repository.FilmShowRepository;
import com.movie.main.repository.OrderDataFilmRepository;
import com.movie.main.repository.OrderDataItemRepository;
import com.movie.main.repository.OrderDecoratorsOfflineServiceRepository;
import com.movie.main.repository.OrderDecoratorsPointUsageRepository;
import com.movie.main.repository.OrderDecoratorsPromotionRepository;
import com.movie.main.repository.OrderItemRepository;
import com.movie.main.repository.OrderTicketRepository;
import com.movie.main.repository.PromotionRepository;
import com.movie.main.repository.RoomSeatRepository;
import com.movie.main.repository.StripePaymentRepository;
import com.movie.main.repository.TicketTypeRepository;
import com.movie.main.resource.ResourceStrings;
import com.movie.main.ulti.Expected;
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.ApiException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.IdempotencyException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.PermissionException;
import com.stripe.exception.RateLimitException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {
    public enum CreationError {
        ENTITY_NOT_EXISTS,
        INSUFFICIENT_LOYAL_POINT,
        UNSPECIFIED,
    }

    public enum PaymentError {
        AUTH_ERROR,
        INVALID_REQUEST,
        CARD_DECLINED,
        NETWORK_ERROR,
        SERVER_ERROR,
        RATE_LIMIT,
        IDEMPOTENCY,
        PERMISSION_DENIED,
        ENTITY_NOT_EXISTS,
        INTERNAL_ERROR,
        UNSPECIFIED,
    }

    public enum HandleWebhookError {
        PAYMENT_REQUIRED,
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

    @NotNull
    private final OrderTicketRepository orderTicketRepository;

    @NotNull
    private final OrderItemRepository orderItemRepository;

    @NotNull
    private final StripePaymentRepository stripePaymentRepository;

    @NotNull
    private final ApplicationEventPublisher publisher;

    public OrderService(
            @NotNull final CustomerRepository customerRepository,
            @NotNull final FilmShowRepository filmShowRepository,
            @NotNull final TicketTypeRepository ticketTypeRepository,
            @NotNull final RoomSeatRepository roomSeatRepository,
            @NotNull final AdditionalItemRepository additionalItemRepository,
            @NotNull final PromotionRepository promotionRepository,
            @NotNull final ParamService paramService,
            @NotNull final CustomerOrderRepository customerOrderRepository,
            @NotNull final OrderDataFilmRepository orderDataFilmRepository,
            @NotNull final OrderDataItemRepository orderDataItemRepository,
            @NotNull final OrderDecoratorsOfflineServiceRepository orderDecoratorsOfflineServiceRepository,
            @NotNull final OrderDecoratorsPointUsageRepository orderDecoratorsPointUsageRepository,
            @NotNull final OrderDecoratorsPromotionRepository orderDecoratorsPromotionRepository,
            @NotNull final OrderTicketRepository orderTicketRepository,
            @NotNull final OrderItemRepository orderItemRepository,
            @NotNull final StripePaymentRepository stripePaymentRepository,
            @NotNull final ApplicationEventPublisher publisher) {
        this.customerRepository = customerRepository;
        this.filmShowRepository = filmShowRepository;
        this.ticketTypeRepository = ticketTypeRepository;
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
        this.orderTicketRepository = orderTicketRepository;
        this.orderItemRepository = orderItemRepository;
        this.stripePaymentRepository = stripePaymentRepository;
        this.publisher = publisher;
    }

    @NotNull
    public static OrderResponseDto getResponseDtoFrom(
            @NotNull final CustomerOrder customerOrder,
            @Nullable final OrderDataFilm orderDataFilm,
            @Nullable final OrderDataItem orderDataItem,
            @Nullable final OrderDecoratorsOfflineService orderDecoratorsOfflineService,
            @Nullable final OrderDecoratorsPointUsage orderDecoratorsPointUsage,
            @Nullable final OrderDecoratorsPromotion orderDecoratorsPromotion) {
        final int filmShowId;
        final Set<OrderTicket> orderTickets;
        final Set<RoomSeat> roomSeats;
        if (orderDataFilm == null) {
            filmShowId = 0;
            orderTickets = Collections.emptySet();
            roomSeats = Collections.emptySet();
        }
        else {
            filmShowId = orderDataFilm.getFilmShow().getId();
            orderTickets = orderDataFilm.getOrderTickets();
            roomSeats = orderDataFilm.getRoomSeats();
        }

        final Set<TicketResponseDto> ticketDtos = HashSet.newHashSet(orderTickets.size());
        for (final var orderTicket : orderTickets) {
            ticketDtos.add(
                    new TicketResponseDto(
                            orderTicket.getId(),
                            orderTicket.getName(),
                            orderTicket.getPrice(),
                            orderTicket.getQuantity()));
        }

        final Set<SeatResponseDto> seatDtos = HashSet.newHashSet(roomSeats.size());
        for (final var roomSeat : roomSeats) {
            seatDtos.add(
                    new SeatResponseDto(
                            roomSeat.getId(),
                            roomSeat.getName()));
        }

        final Set<OrderItem> orderItems;
        if (orderDataItem == null) {
            orderItems = Collections.emptySet();
        }
        else {
            orderItems = orderDataItem.getOrderItems();
        }

        final Set<ItemResponseDto> itemDtos = HashSet.newHashSet(orderItems.size());
        for (final var orderItem : orderItems) {
            itemDtos.add(
                    new ItemResponseDto(
                            orderItem.getId(),
                            orderItem.getName(),
                            orderItem.getPrice(),
                            orderItem.getQuantity()));
        }

        final Set<Promotion> promotions;
        if (orderDecoratorsPromotion == null) {
            promotions = Collections.emptySet();
        }
        else {
            promotions = orderDecoratorsPromotion.getPromotions();
        }

        final int pointUsage;
        if (orderDecoratorsPointUsage == null) {
            pointUsage = 0;
        }
        else {
            pointUsage = orderDecoratorsPointUsage.getPointUsed();
        }

        final Set<Integer> promotionIds = HashSet.newHashSet(promotions.size());
        for (final var promotion : promotions) {
            promotionIds.add(promotion.getId());
        }

        return new OrderResponseDto(
                customerOrder.getId(),
                customerOrder.getCustomer().getId(),
                customerOrder.getTotalPrice(),
                customerOrder.getTotalPriceAfterDiscount(),
                filmShowId,
                ticketDtos,
                seatDtos,
                itemDtos,
                promotionIds,
                pointUsage,
                customerOrder.getVerifyCode());
    }

    @NotNull
    public Page<@NotNull OrderResponseDto> findAll(@NotNull final Pageable pageable) {
        final var customerOrders = this.customerOrderRepository.findAll(pageable);
        final List<OrderResponseDto> dtoList = new ArrayList<>(customerOrders.getNumberOfElements());

        for (final var customerOrder : customerOrders) {
            final var customerOrderId = customerOrder.getId();
            final var orderDataFilm = this.orderDataFilmRepository.findById(customerOrderId).orElse(null);
            final var orderDataItem = this.orderDataItemRepository.findById(customerOrderId).orElse(null);
            final var orderDecoratorsOfflineService = this.orderDecoratorsOfflineServiceRepository
                    .findById(customerOrderId).orElse(null);
            final var orderDecoratorsPointUsage = this.orderDecoratorsPointUsageRepository
                    .findById(customerOrderId).orElse(null);
            final var orderDecoratorsPromotion = this.orderDecoratorsPromotionRepository
                    .findById(customerOrderId).orElse(null);

            dtoList.add(
                    OrderService.getResponseDtoFrom(
                            customerOrder,
                            orderDataFilm,
                            orderDataItem,
                            orderDecoratorsOfflineService,
                            orderDecoratorsPointUsage,
                            orderDecoratorsPromotion));
        }

        return new PageImpl<>(dtoList, pageable, customerOrders.getTotalElements());
    }

    @NotNull
    public Page<@NotNull OrderResponseDto> findAllByCustomerId(
            final int customerId,
            @NotNull final Pageable pageable) {
        final var customerOrders = this.customerOrderRepository.findAllByCustomerId(customerId, pageable);
        final List<OrderResponseDto> dtoList = new ArrayList<>(customerOrders.getNumberOfElements());

        for (final var customerOrder : customerOrders) {
            final var customerOrderId = customerOrder.getId();
            final var orderDataFilm = this.orderDataFilmRepository.findById(customerOrderId).orElse(null);
            final var orderDataItem = this.orderDataItemRepository.findById(customerOrderId).orElse(null);
            final var orderDecoratorsOfflineService = this.orderDecoratorsOfflineServiceRepository
                    .findById(customerOrderId).orElse(null);
            final var orderDecoratorsPointUsage = this.orderDecoratorsPointUsageRepository
                    .findById(customerOrderId).orElse(null);
            final var orderDecoratorsPromotion = this.orderDecoratorsPromotionRepository
                    .findById(customerOrderId).orElse(null);

            dtoList.add(
                    OrderService.getResponseDtoFrom(
                            customerOrder,
                            orderDataFilm,
                            orderDataItem,
                            orderDecoratorsOfflineService,
                            orderDecoratorsPointUsage,
                            orderDecoratorsPromotion));
        }

        return new PageImpl<>(dtoList, pageable, customerOrders.getTotalElements());
    }

    @Nullable
    public OrderResponseDto findById(final int id) {
        final var customerOrder = this.customerOrderRepository.findById(id).orElse(null);
        if (customerOrder == null) {
            return null;
        }

        final var orderDataFilm = this.orderDataFilmRepository.findById(id).orElse(null);
        if (orderDataFilm == null) {
            return null;
        }

        final var orderDataItem = this.orderDataItemRepository.findById(id).orElse(null);
        if (orderDataItem == null) {
            return null;
        }

        final var orderDecoratorsOfflineService = this.orderDecoratorsOfflineServiceRepository
                .findById(id).orElse(null);
        if (orderDecoratorsOfflineService == null) {
            return null;
        }

        final var orderDecoratorsPointUsage = this.orderDecoratorsPointUsageRepository
                .findById(id).orElse(null);
        if (orderDecoratorsPointUsage == null) {
            return null;
        }

        final var orderDecoratorsPromotion = this.orderDecoratorsPromotionRepository
                .findById(id).orElse(null);
        if (orderDecoratorsPromotion == null) {
            return null;
        }

        return OrderService.getResponseDtoFrom(
                customerOrder,
                orderDataFilm,
                orderDataItem,
                orderDecoratorsOfflineService,
                orderDecoratorsPointUsage,
                orderDecoratorsPromotion);
    }

    @Transactional
    public Expected<OrderResponseDto, CreationError> create(
            @NotNull final OrderRequestDto requestDto,
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
        final var pointUsage = requestDto.getPointUsage();
        if (customerLoyalPoint < pointUsage) {
            return Expected.failure(CreationError.INSUFFICIENT_LOYAL_POINT);
        }
        customer.setLoyalPoint(customerLoyalPoint - pointUsage);

        final var filmShow = this.filmShowRepository.findByIdAndDeletedFalse(requestDto.getFilmShowId()).orElse(null);
        if (filmShow == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var tickets = requestDto.getTickets();
        final Set<OrderTicket> orderTickets = HashSet.newHashSet(tickets.size());
        for (final var ticket : tickets) {
            final var ticketType = this.ticketTypeRepository.findById(ticket.getTypeId()).orElse(null);

            if (ticketType == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            final var orderTicket = this.orderTicketRepository.saveAndFlush(
                    new OrderTicket(
                            ticketType.getTitle(),
                            ticket.getQuantity(),
                            ticketType.getPrice()));

            orderTickets.add(orderTicket);
        }

        final var seatIds = requestDto.getSeatIds();
        final Set<RoomSeat> roomSeats = HashSet.newHashSet(seatIds.size());
        for (final var seatId : seatIds) {
            final var roomSeat = this.roomSeatRepository.findById(seatId).orElse(null);

            if (roomSeat == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            roomSeats.add(roomSeat);
        }

        final var items = requestDto.getItems();
        final Set<OrderItem> orderItems = HashSet.newHashSet(items.size());
        for (final var item : items) {
            final var additionalItem = this.additionalItemRepository.findByIdAndDeletedFalse(item.getId()).orElse(null);

            if (additionalItem == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            final var orderItem = this.orderItemRepository.saveAndFlush(
                    new OrderItem(
                            additionalItem.getName(),
                            item.getQuantity(),
                            additionalItem.getPrice()));

            orderItems.add(orderItem);
        }

        final var promotionIds = requestDto.getPromotionIds();
        final Set<Promotion> promotions = HashSet.newHashSet(promotionIds.size());
        for (final var promotionId : promotionIds) {
            final var promotion = this.promotionRepository.findById(promotionId).orElse(null);

            if (promotion == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            promotions.add(promotion);
        }

        try {
            final var customerOrder = this.customerOrderRepository
                    .saveAndFlush(new CustomerOrder(
                            LocalDate.now(),
                            UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.getDefault()),
                            requestDto.getTotalPrice(),
                            requestDto.getTotalPriceAfterDiscount(),
                            customer));

            final var orderDataFilm = this.orderDataFilmRepository
                    .saveAndFlush(new OrderDataFilm(
                            customerOrder,
                            LocalDate.now(),
                            LocalTime.now(),
                            filmShow,
                            roomSeats,
                            orderTickets));

            final var orderDataItem = this.orderDataItemRepository
                    .saveAndFlush(new OrderDataItem(
                            customerOrder,
                            orderItems));

            final var orderDecoratorsOfflineService = this.orderDecoratorsOfflineServiceRepository
                    .saveAndFlush(new OrderDecoratorsOfflineService(
                            customerOrder,
                            false,
                            false,
                            null,
                            null));

            final var orderDecoratorsPointUsage = this.orderDecoratorsPointUsageRepository
                    .saveAndFlush(new OrderDecoratorsPointUsage(
                            customerOrder,
                            pointUsage,
                            param.getLoyalPointPointToReducedPriceRatio()));

            final var orderDecoratorsPromotion = this.orderDecoratorsPromotionRepository
                    .saveAndFlush(new OrderDecoratorsPromotion(
                            customerOrder,
                            promotions));

            return Expected.success(
                    OrderService.getResponseDtoFrom(
                            customerOrder,
                            orderDataFilm,
                            orderDataItem,
                            orderDecoratorsOfflineService,
                            orderDecoratorsPointUsage,
                            orderDecoratorsPromotion));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    public Expected<String, PaymentError> createStripeCheckoutSession(
            final OrderRequestDto requestDto,
            final int customerId) {
        try {
            final var customer = this.customerRepository.findById(customerId).orElse(null);
            if (customer == null) {
                return Expected.failure(PaymentError.ENTITY_NOT_EXISTS);
            }

            final var mapper = new ObjectMapper();
            final var metaData = Map.ofEntries(
                    Map.entry(OrderRequestDto.Fields.filmShowId,
                            String.valueOf(requestDto.getFilmShowId())),
                    Map.entry(OrderRequestDto.Fields.seatIds,
                            requestDto.getSeatIds().stream().map(Object::toString).collect(Collectors.joining(","))),
                    Map.entry(OrderRequestDto.Fields.totalPrice,
                            String.valueOf(requestDto.getTotalPrice())),
                    Map.entry(OrderRequestDto.Fields.totalPriceAfterDiscount,
                            String.valueOf(requestDto.getTotalPriceAfterDiscount())),
                    Map.entry(OrderRequestDto.Fields.pointUsage,
                            String.valueOf(requestDto.getPointUsage())),
                    Map.entry(OrderRequestDto.Fields.promotionIds,
                            requestDto.getPromotionIds().stream().map(Object::toString)
                                    .collect(Collectors.joining(","))),
                    Map.entry(OrderRequestDto.Fields.tickets,
                            mapper.writeValueAsString(requestDto.getTickets())),
                    Map.entry(OrderRequestDto.Fields.items,
                            mapper.writeValueAsString(requestDto.getItems())));

            List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
            lineItems.add(SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(ResourceStrings.STRIPE_CURRENCY)
                            .setUnitAmount(Long.valueOf(requestDto.getTotalPriceAfterDiscount()))
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Thanh toán tiền vé xem phim")
                                    .build())
                            .build())
                    .build());

            final var params = SessionCreateParams.builder()
                    .addAllLineItem(lineItems)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .putAllMetadata(metaData)
                    .setSuccessUrl("http://localhost:5173/order-success")
                    .setCancelUrl("http://localhost:5173/order-failed")
                    .build();

            final var session = Session.create(params);
            final var stripePayment = new StripePayment(
                    session.getId(),
                    customer,
                    StripePayment.Status.from(session.getStatus()),
                    requestDto.getTotalPriceAfterDiscount(),
                    Instant.now());

            this.stripePaymentRepository.save(stripePayment);
            return Expected.success(session.getId());
        }
        catch (final PermissionException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.PERMISSION_DENIED);
        }
        catch (final AuthenticationException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.AUTH_ERROR);
        }
        catch (final RateLimitException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.RATE_LIMIT);
        }
        catch (final InvalidRequestException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.INVALID_REQUEST);

        }
        catch (final CardException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.CARD_DECLINED);

        }
        catch (final ApiConnectionException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.NETWORK_ERROR);

        }
        catch (final ApiException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.SERVER_ERROR);

        }
        catch (final IdempotencyException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.IDEMPOTENCY);

        }
        catch (final StripeException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.INTERNAL_ERROR);
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.UNSPECIFIED);
        }
    }

    public Expected<OrderResponseDto, HandleWebhookError> handleStripeWebhook(String sigHeader, String payload) {
        try {
            final var event = Webhook.constructEvent(payload, sigHeader, ResourceStrings.STRIPE_WEBHOOK_SECRET);
            switch (event.getType()) {
                case "checkout.session.completed": {
                    final var session = (Session) event.getDataObjectDeserializer().getObject().get();
                    final var responseDto = this.handleStripeWebhookSessionCompleted(session);
                    if (responseDto != null) {
                        this.publisher.publishEvent(new OrderCreatedEvent(responseDto));
                        return Expected.success(responseDto);
                    }
                    return Expected.failure(HandleWebhookError.UNSPECIFIED);
                }

                case "payment_intent.payment_failed": {
                    return Expected.failure(HandleWebhookError.PAYMENT_REQUIRED);
                }

                default: {
                    return Expected.failure(HandleWebhookError.UNSPECIFIED);
                }
            }
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(HandleWebhookError.UNSPECIFIED);
        }
    }

    @Nullable
    private OrderResponseDto handleStripeWebhookSessionCompleted(final Session session) {
        final var stripePayment = this.stripePaymentRepository.findByPaymentIntentId(session.getId()).orElse(null);
        if (stripePayment == null) {
            return null;
        }

        final var requestDto = this.parseCreateOrderRequestDtoFromMetaData(session.getMetadata());
        if (requestDto == null) {
            return null;
        }
        final var customerId = stripePayment.getCustomer().getId();

        return this.create(requestDto, customerId).getValue();
    }

    @Nullable
    public OrderRequestDto parseCreateOrderRequestDtoFromMetaData(final Map<String, String> metaData) {
        try {
            final var filmShowId = Integer.parseInt(metaData.get(OrderRequestDto.Fields.filmShowId));
            final var totalPrice = Integer.parseInt(metaData.get(OrderRequestDto.Fields.totalPrice));
            final var totalPriceAfterDiscount = Integer.parseInt(
                    metaData.get(OrderRequestDto.Fields.totalPriceAfterDiscount));
            final var pointUsage = Integer.parseInt(metaData.get(OrderRequestDto.Fields.pointUsage));

            final var seatIds = Arrays.stream(metaData.get(OrderRequestDto.Fields.seatIds).split(","))
                    .filter(s -> !s.isBlank()).map(Integer::parseInt).collect(Collectors.toSet());

            final var promotionIds = Arrays.stream(metaData.get(OrderRequestDto.Fields.promotionIds)
                    .split(",")).filter((s -> !s.isBlank())).map(Integer::parseInt).collect(Collectors.toSet());

            final var mapper = new ObjectMapper();
            final var ticketDtos = new HashSet<>(Arrays.asList(mapper.readValue(
                    metaData.get(OrderRequestDto.Fields.tickets),
                    OrderRequestDto.TicketRequestDto[].class)));
            final var itemDtos = new HashSet<>(Arrays.asList(mapper.readValue(
                    metaData.get(OrderRequestDto.Fields.items), OrderRequestDto.ItemRequestDto[].class)));

            return new OrderRequestDto(
                    totalPrice,
                    totalPriceAfterDiscount,
                    filmShowId,
                    ticketDtos,
                    seatIds,
                    itemDtos,
                    promotionIds,
                    pointUsage);
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }
}

package com.movie.main.service;

import java.util.HashSet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.OrderDecoratorsPromotionRequestDto;
import com.movie.main.entity.OrderDecoratorsPromotion;
import com.movie.main.entity.Promotion;
import com.movie.main.repository.OrderDecoratorsPromotionRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderDecoratorsPromotionService {
    public enum CreationError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    @NotNull
    private final OrderDecoratorsPromotionRepository repository;

    @NotNull
    private final CustomerOrderService customerOrderService;

    @NotNull
    private final PromotionService promotionService;

    public OrderDecoratorsPromotionService(
            @NotNull final OrderDecoratorsPromotionRepository repository,
            @NotNull final CustomerOrderService customerOrderService,
            @NotNull final PromotionService promotionService) {
        this.repository = repository;
        this.customerOrderService = customerOrderService;
        this.promotionService = promotionService;
    }

    @NotNull
    public Page<@NotNull OrderDecoratorsPromotion> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public OrderDecoratorsPromotion findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<OrderDecoratorsPromotion, CreationError> create(
            @NotNull final OrderDecoratorsPromotionRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var promotionIds = requestDto.promotionIds();
        final HashSet<Promotion> promotions = HashSet.newHashSet(promotionIds.size());
        for (final var promotionId : promotionIds) {
            final var promotion = this.promotionService.findById(promotionId);

            if (promotion == null) {
                return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
            }

            promotions.add(promotion);
        }

        final var newOrderDecoratorsPromotion = new OrderDecoratorsPromotion(
                customerOrder,
                promotions);

        try {
            return Expected.success(this.repository.save(newOrderDecoratorsPromotion));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<OrderDecoratorsPromotion, UpdateError> updateById(
            final int id,
            @NotNull final OrderDecoratorsPromotionRequestDto requestDto) {
        final var customerOrder = this.customerOrderService.findById(requestDto.customerOrderId());
        if (customerOrder == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var orderDecoratorsPromotion = this.findById(id);
        if (orderDecoratorsPromotion == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var promotionIds = requestDto.promotionIds();
        final HashSet<Promotion> promotions = HashSet.newHashSet(promotionIds.size());
        for (final var promotionId : promotionIds) {
            final var promotion = this.promotionService.findById(promotionId);

            if (promotion == null) {
                return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
            }

            promotions.add(promotion);
        }

        orderDecoratorsPromotion.setCustomerOrder(customerOrder);
        orderDecoratorsPromotion.setPromotions(promotions);

        try {
            return Expected.success(this.repository.save(orderDecoratorsPromotion));
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

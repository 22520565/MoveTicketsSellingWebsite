package com.movie.main.dto.request;

import org.hibernate.validator.constraints.Range;

import com.movie.main.entity.Param;

public record ParamRequestDto(
        @Range(min = Param.MinLoyalPointOrderToPointRatio, max = Param.MaxLoyalPointOrderToPointRatio) int loyalPointOrderToPointRatio,
        @Range(min = Param.MinLoyalPointPointToReducedPriceRatio, max = Param.MaxLoyalPointPointToReducedPriceRatio) int loyalPointPointToReducedPriceRatio,
        @Range(min = Param.MinLoyalPointMinimumValueToUseLoyalPoint, max = Param.MaxLoyalPointMinimumValueToUseLoyalPoint) int loyalPointMinimumValueToUseLoyalPoint,
        @Range(min = Param.MinLoyalPointMaximumPointUseInOneGo, max = Param.MaxLoyalPointMaximumPointUseInOneGo) int loyalPointMaximumPointUseInOneGo,
        @Range(min = Param.MinMaximumDiscountRate, max = Param.MaxMaximumDiscountRate) int maximumDiscountRate,
        @Range(min = Param.MinAddedPriceForVipSeat, max = Param.MaxAddedPriceForVipSeat) int addedPriceForVipSeat) {}

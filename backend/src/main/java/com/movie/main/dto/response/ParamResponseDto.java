package com.movie.main.dto.response;

public record ParamResponseDto(
        int loyalPointOrderToPointRatio,
        int loyalPointPointToReducedPriceRatio,
        int loyalPointMinimumValueToUseLoyalPoint,
        int loyalPointMaximumPointUseInOneGo,
        int maximumDiscountRate,
        int addedPriceForVipSeat) {}

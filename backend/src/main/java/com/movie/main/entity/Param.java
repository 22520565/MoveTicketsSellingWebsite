package com.movie.main.entity;

import org.hibernate.validator.constraints.Range;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class Param {
    public static final int DefaultId = 1;
    public static final int MinLoyalPointOrderToPointRatio = 1;
    public static final int MaxLoyalPointOrderToPointRatio = 1_000;
    public static final int DefaultLoyalPointOrderToPointRatio = 10;
    public static final int MinLoyalPointPointToReducedPriceRatio = 1;
    public static final int MaxLoyalPointPointToReducedPriceRatio = 1_000;
    public static final int DefaultLoyalPointPointToReducedPriceRatio = 50;
    public static final int MinLoyalPointMinimumValueToUseLoyalPoint = 1;
    public static final int MaxLoyalPointMinimumValueToUseLoyalPoint = 1_000_000;
    public static final int DefaultLoyalPointMinimumValueToUseLoyalPoint = 200_000;
    public static final int MinLoyalPointMaximumPointUseInOneGo = 1;
    public static final int MaxLoyalPointMaximumPointUseInOneGo = 1_000_000;
    public static final int DefaultLoyalPointMaximumPointUseInOneGo = 50_000;
    public static final int MinMaximumDiscountRate = 1;
    public static final int MaxMaximumDiscountRate = 100;
    public static final int DefaultMaximumDiscountRate = 40;
    public static final int MinAddedPriceForVipSeat = 1;
    public static final int MaxAddedPriceForVipSeat = 1_000;
    public static final int DefaultAddedPriceForVipSeat = 20;

    @Id
    @Range(min = DefaultId, max = DefaultId)
    private final int id = DefaultId;

    @Column(nullable = false)
    @Range(min = MinLoyalPointOrderToPointRatio, max = MaxLoyalPointOrderToPointRatio)
    private int loyalPointOrderToPointRatio = DefaultLoyalPointOrderToPointRatio;

    @Column(nullable = false)
    @Range(min = MinLoyalPointPointToReducedPriceRatio, max = MaxLoyalPointPointToReducedPriceRatio)
    private int loyalPointPointToReducedPriceRatio = DefaultLoyalPointPointToReducedPriceRatio;

    @Column(nullable = false)
    @Range(min = MinLoyalPointMinimumValueToUseLoyalPoint, max = MaxLoyalPointMinimumValueToUseLoyalPoint)
    private int loyalPointMinimumValueToUseLoyalPoint = DefaultLoyalPointMinimumValueToUseLoyalPoint;

    @Column(nullable = false)
    @Range(min = MinLoyalPointMaximumPointUseInOneGo, max = MaxLoyalPointMaximumPointUseInOneGo)
    private int loyalPointMaximumPointUseInOneGo = DefaultLoyalPointMaximumPointUseInOneGo;

    @Column(nullable = false)
    @Range(min = MinMaximumDiscountRate, max = MaxMaximumDiscountRate)
    private int maximumDiscountRate = DefaultMaximumDiscountRate;

    @Column(nullable = false)
    @Range(min = MinAddedPriceForVipSeat, max = MaxAddedPriceForVipSeat)
    private int addedPriceForVipSeat = DefaultAddedPriceForVipSeat;
}

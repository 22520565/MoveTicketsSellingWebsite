package com.movie.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class OrderDecoratorsPointUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @Column(nullable = false)
    private int pointUsed = 0;

    @Column(nullable = false)
    private int pointToMoneyRatio = 0;

    public OrderDecoratorsPointUsage(final int pointUsed, final int pointToMoneyRatio) {
        this.pointUsed = pointUsed;
        this.pointToMoneyRatio = pointToMoneyRatio;
    }
}

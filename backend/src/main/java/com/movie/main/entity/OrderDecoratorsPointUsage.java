package com.movie.main.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.NotNull;
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
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @MapsId
    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @Setter(value = AccessLevel.NONE)
    @NotNull
    private CustomerOrder customerOrder = null;

    @Column(nullable = false)
    private int pointUsed = 0;

    @Column(nullable = false)
    private int pointToMoneyRatio = 0;

    public OrderDecoratorsPointUsage(
            final CustomerOrder customerOrder,
            final int pointUsed,
            final int pointToMoneyRatio) {
        this.customerOrder = customerOrder;
        this.pointUsed = pointUsed;
        this.pointToMoneyRatio = pointToMoneyRatio;
    }

    public void setCustomerOrder(@NotNull final CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }
}

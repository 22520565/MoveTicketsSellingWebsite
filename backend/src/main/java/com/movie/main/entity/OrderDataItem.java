package com.movie.main.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class OrderDataItem extends IntegerIdentifiableEntity {
    @MapsId
    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @NotNull
    private CustomerOrder customerOrder = null;

    @Column(nullable = false)
    @Min(0)
    private int quantity = 0;

    @Column(nullable = false)
    @Min(0)
    private int price = 0;

    public OrderDataItem(final int quantity, final int price, final CustomerOrder customerOrder) {
        super(customerOrder.getId());
        this.customerOrder = customerOrder;
        this.quantity = quantity;
        this.price = price;
    }
}

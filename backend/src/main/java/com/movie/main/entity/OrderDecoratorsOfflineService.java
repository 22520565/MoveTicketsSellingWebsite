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
public class OrderDecoratorsOfflineService {
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
    private boolean printed = false;

    @Column(nullable = false)
    private boolean served = false;

    @Column
    private String invalidReasonPrinted = "";

    @Column
    private String invalidReasonServed = "";

    public OrderDecoratorsOfflineService(
            final CustomerOrder customerOrder,
            final boolean printed,
            final boolean served,
            final String invalidReasonPrinted,
            final String invalidReasonServed) {
        this.id = customerOrder.getId();
        this.customerOrder = customerOrder;
        this.printed = printed;
        this.served = served;
        this.invalidReasonPrinted = invalidReasonPrinted;
        this.invalidReasonServed = invalidReasonServed;
    }

    public void setCustomerOrder(@NotNull final CustomerOrder customerOrder) {
        this.id = customerOrder.getId();
        this.customerOrder = customerOrder;
    }
}

package com.movie.main.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class OrderDataItem {
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

    @ManyToMany(fetch = FetchType.EAGER)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @NotNull
    private Set<@NotNull OrderItem> orderItems = new HashSet<>();

    public OrderDataItem(
            final CustomerOrder customerOrder,
            final Set<@NotNull OrderItem> orderItems) {
        this.id = this.customerOrder.getId();
        this.customerOrder = customerOrder;
        this.orderItems = new HashSet<>(orderItems);
    }

    public void setCustomerOrder(@NotNull final CustomerOrder customerOrder) {
        this.id = customerOrder.getId();
        this.customerOrder = customerOrder;
    }

    public Set<OrderItem> getOrderItems() {
        return new HashSet<>(orderItems);
    }

    public void setOrderItems(final Set<OrderItem> orderItems) {
        this.orderItems = new HashSet<>(orderItems);
    }
}

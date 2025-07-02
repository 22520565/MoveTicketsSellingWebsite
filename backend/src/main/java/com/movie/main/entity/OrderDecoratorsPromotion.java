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
public class OrderDecoratorsPromotion {
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
    private Set<@NotNull Promotion> promotions = new HashSet<>();

    public OrderDecoratorsPromotion(
            final CustomerOrder customerOrder,
            final Set<@NotNull Promotion> promotions) {
        this.customerOrder = customerOrder;
        this.promotions = new HashSet<>(promotions);
    }

    public void setCustomerOrder(@NotNull final CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public Set<@NotNull Promotion> getPromotions() {
        return new HashSet<>(this.promotions);
    }

    public void setPromotions(final Set<@NotNull Promotion> promotions) {
        this.promotions = new HashSet<>(promotions);
    }
}

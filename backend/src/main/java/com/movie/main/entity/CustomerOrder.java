package com.movie.main.entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class CustomerOrder extends AbstractIntegerIdentifiableEntity {
    @Column(nullable = false)
    private LocalDate date = LocalDate.now();

    @Column(nullable = true)
    private String verifyCode = "";

    @Column(nullable = false)
    @Min(0)
    private int totalPrice = 0;

    @Column(nullable = false)
    @Min(0)
    private int totalPriceAfterDiscount = 0;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Customer customer = null;
}

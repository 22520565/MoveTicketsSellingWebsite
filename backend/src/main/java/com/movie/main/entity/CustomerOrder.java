package com.movie.main.entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class CustomerOrder {
    public static final int VerifyCodeLength = 8;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @Column(nullable = false)
    private LocalDate date = LocalDate.now();

    @Column(nullable = false, length = VerifyCodeLength)
    @Size(min = VerifyCodeLength, max = VerifyCodeLength)
    @NotBlank
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

    public CustomerOrder(
            final LocalDate date,
            final String verifyCode,
            final int totalPrice,
            final int totalPriceAfterDiscount,
            final Customer customer) {
        this.date = date;
        this.verifyCode = verifyCode;
        this.totalPrice = totalPrice;
        this.totalPriceAfterDiscount = totalPriceAfterDiscount;
        this.customer = customer;
    }
}

package com.movie.main.entity;

import org.hibernate.validator.constraints.Range;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldNameConstants
public class TicketType {
    public static final int MaxLengthName = 30;
    public static final int MinLoyalPointRate = 0;
    public static final int MaxLoyalPointRate = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @Column(nullable = false, length = MaxLengthName)
    @NotBlank
    private String title = "";

    @Column(nullable = false)
    @Min(1)
    private int price = 0;

    @Column(nullable = false)
    private boolean isPair = false;

    @Column(nullable = false)
    @Range(min = MinLoyalPointRate, max = MaxLoyalPointRate)
    private int loyalPointRate = 0;

    public TicketType(
            final String title,
            final int price,
            final boolean isPair,
            final int loyalPointRate) {
        this.title = title;
        this.price = price;
        this.isPair = isPair;
        this.loyalPointRate = loyalPointRate;
    }
}

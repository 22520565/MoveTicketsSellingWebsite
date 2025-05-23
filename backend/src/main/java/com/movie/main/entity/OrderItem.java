package com.movie.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class OrderItem {
    public static final int MaxLengthName = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @Column(nullable = false, length = MaxLengthName)
    @NotBlank
    @Size(max = MaxLengthName)
    private String name = "";

    @Column(nullable = false)
    @Min(0)
    private int quantity = 0;

    @Column(nullable = false)
    @Min(0)
    private int price = 0;

    public OrderItem(final String name, final int quantity, final int price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}

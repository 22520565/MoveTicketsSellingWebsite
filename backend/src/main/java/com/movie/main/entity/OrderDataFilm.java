package com.movie.main.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
public class OrderDataFilm {
    public static final int VerifyCodeLength = 8;

    @Id
    @Column(nullable = false, unique = true, updatable = false)
    @Setter(value = AccessLevel.PACKAGE)
    private int id = 0;

    @MapsId
    @OneToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY, optional = false)
    @Setter(value = AccessLevel.PACKAGE)
    @NotNull
    private CustomerOrder customerOrder = null;

    @Column(nullable = false, length = Film.MaxLengthName)
    @Size(min = Film.MinLengthName, max = Film.MaxLengthName)
    @NotBlank
    private String filmName = "";

    @Column(nullable = false, length = AgeRestriction.MaxLengthName)
    @Size(min = AgeRestriction.MaxLengthName, max = AgeRestriction.MaxLengthName)
    @NotBlank
    private String ageRestriction = "";

    @Column(nullable = false)
    @NotNull
    private LocalDate date = LocalDate.now();

    @Column(nullable = false)
    @NotNull
    private LocalTime time = LocalTime.now();

    public OrderDataFilm(final CustomerOrder customerOrder) {
        this.id = customerOrder.getId();
        this.customerOrder = customerOrder;
    }
}

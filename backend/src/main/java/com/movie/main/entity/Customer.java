package com.movie.main.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Customer extends User {
    @Column(nullable = false)
    @Min(0)
    private int loyalPoint = 0;

    public Customer(
            final String name,
            final LocalDate birthDate,
            final String email,
            final String phoneNumber,
            final String username,
            final String hashedPassword) {
        super(name, birthDate, email, phoneNumber, username, hashedPassword);
    }
}

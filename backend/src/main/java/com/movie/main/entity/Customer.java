package com.movie.main.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Customer extends AbstractUserDetail {
    public static final UserRole userRole = UserRole.Customer;

    public Customer(final User user) {
        super(user);
    }
}

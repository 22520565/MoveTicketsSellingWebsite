package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.StripePayment;

import jakarta.validation.constraints.NotNull;

@Repository
public interface StripePaymentRepository extends JpaRepository<StripePayment, Integer> {
    @NotNull
    Optional<StripePayment> findByPaymentIntentId(final String paymentIntentId);
}

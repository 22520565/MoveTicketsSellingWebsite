package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.OrderDataFilm;

import jakarta.validation.constraints.NotNull;

@Repository
public interface OrderDataFilmRepository extends JpaRepository<OrderDataFilm, Integer> {
    @Query("SELECT o FROM OrderDataFilm o WHERE (o.id = :id) AND (o.customerOrder.customer.id = : customerId)")
    @NotNull
    Optional<OrderDataFilm> findByIdAndCustomerId(@Param("id") final int id, @Param("customerId") final int customerId);
}

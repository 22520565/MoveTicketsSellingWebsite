package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Promotion;

import jakarta.validation.constraints.NotNull;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    @Query("SELECT DISTINCT p FROM Promotion p "
            + "WHERE NOT(p.paused) AND "
            + "CURRENT_DATE BETWEEN p.beginDate AND p.endDate "
            + "ORDER BY p.endDate ASC, p.beginDate DESC")
    @NonNull
    Page<@NotNull Promotion> findAllActivePromotionOrderByDate(@NotNull Pageable pageable);

    @Query("SELECT DISTINCT p FROM Promotion p "
            + "WHERE (p.paused) AND "
            + "CURRENT_DATE BETWEEN p.beginDate AND p.endDate "
            + "ORDER BY p.endDate ASC, p.beginDate DESC")
    @NonNull
    Page<@NotNull Promotion> findAllInactivePromotionOrderByDate(@NotNull Pageable pageable);

    @Query("SELECT DISTINCT p FROM Promotion p "
            + "WHERE CURRENT_DATE NOT BETWEEN p.beginDate AND p.endDate "
            + "ORDER BY p.endDate ASC, p.beginDate DESC")
    @NonNull
    Page<@NotNull Promotion> findAllExpiredPromotionOrderByDate(@NotNull Pageable pageable);

    @Query("SELECT p FROM Promotion p "
            + "WHERE (p.id = :id) AND "
            + "NOT(p.paused) AND "
            + "CURRENT_DATE BETWEEN p.beginDate AND p.endDate ")
    @NonNull
    Optional<@NotNull Promotion> findActivePromotionById(final int id);
}

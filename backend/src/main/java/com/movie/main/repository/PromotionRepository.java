package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {}

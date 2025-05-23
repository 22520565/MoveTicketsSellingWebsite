package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.OrderDecoratorsPointUsage;

@Repository
public interface OrderDecoratorsPointUsageRepository extends JpaRepository<OrderDecoratorsPointUsage, Integer> {}

package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {}

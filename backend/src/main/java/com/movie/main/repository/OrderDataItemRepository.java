package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.OrderDataItem;

@Repository
public interface OrderDataItemRepository extends JpaRepository<OrderDataItem, Integer> {}

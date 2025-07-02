package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.main.entity.OrderDecoratorsOfflineService;

public interface OrderDecoratorsOfflineServiceRepository
        extends JpaRepository<OrderDecoratorsOfflineService, Integer> {}

package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.main.entity.OrderTicket;

public interface OrderTicketRepository extends JpaRepository<OrderTicket, Integer> {}

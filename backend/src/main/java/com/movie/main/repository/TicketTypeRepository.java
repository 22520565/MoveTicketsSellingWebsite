package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.main.entity.TicketType;

public interface TicketTypeRepository extends JpaRepository<TicketType, Integer> {}

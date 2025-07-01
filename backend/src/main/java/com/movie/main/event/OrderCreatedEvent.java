package com.movie.main.event;

import com.movie.main.dto.response.OrderResponseDto;

public record OrderCreatedEvent(OrderResponseDto customerOrder) {}

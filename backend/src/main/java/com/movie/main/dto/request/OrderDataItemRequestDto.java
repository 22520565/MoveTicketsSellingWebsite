package com.movie.main.dto.request;

import java.util.Set;

import com.movie.main.entity.OrderItem;

import jakarta.validation.constraints.NotNull;

public record OrderDataItemRequestDto(int customerOrderId, @NotNull Set<@NotNull OrderItem> items) {}

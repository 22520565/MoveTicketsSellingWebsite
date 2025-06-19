package com.movie.main.event;

import com.movie.main.entity.CustomerOrder;

public record CustomerOrderCreatedEvent(CustomerOrder customerOrder) {}

package com.movie.main.event;

import com.movie.main.entity.Customer;

public record CustomerCreatedEvent(
        Customer customer) {
}

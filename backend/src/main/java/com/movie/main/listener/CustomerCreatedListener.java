package com.movie.main.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.movie.main.event.CustomerCreatedEvent;

@Async
@Component
public class CustomerCreatedListener {
    @EventListener
    public void handleEvent(final CustomerCreatedEvent event) {}
}

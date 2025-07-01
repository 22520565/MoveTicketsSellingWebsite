package com.movie.main.listener;

import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.movie.main.event.OrderCreatedEvent;
import com.movie.main.repository.CustomerRepository;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Async
@Component
@Slf4j
public class OrderCreatedListener {
    @NotNull
    private JavaMailSender mailSender;

    @NotNull
    private CustomerRepository customerRepository;

    public OrderCreatedListener(
            final JavaMailSender mailSender,
            final CustomerRepository customerRepository) {
        this.mailSender = mailSender;
        this.customerRepository = customerRepository;
    }

    @EventListener
    public void handleEvent(final OrderCreatedEvent event) {
        final var customerId = event.customerOrder().getCustomerId();
        final var customer = this.customerRepository.findByIdAndBlockedFalseAndDeletedFalse(customerId).orElse(null);
        if (customer == null) {
            return;
        }

        final var subject = "Thank you for your order";
        final var content = String.format("Dear {},%nThank you for your order",
                customer.getName());

        try {
            final var message = mailSender.createMimeMessage();
            final var helper = new MimeMessageHelper(message);
            helper.setSubject(subject);
            helper.setText(content);
            this.mailSender.send(message);
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
        }
    }
}

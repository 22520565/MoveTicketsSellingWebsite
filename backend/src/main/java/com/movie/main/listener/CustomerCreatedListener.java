package com.movie.main.listener;

import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.movie.main.event.CustomerCreatedEvent;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Async
@Component
@Slf4j
public class CustomerCreatedListener {
    @NotNull
    private JavaMailSender mailSender;

    public CustomerCreatedListener(
            final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @EventListener
    public void handleEvent(final CustomerCreatedEvent event) {
        final var customer = event.customer();
        final var subject = "Welcome to our website";
        final var content = String.format("Dear %s,%nThank you for being our customer!",
                customer.getName());

        try {
            final var message = mailSender.createMimeMessage();
            final var helper = new MimeMessageHelper(message);
            helper.setSubject(subject);
            helper.setText(content);
            helper.setTo(customer.getEmail());
            this.mailSender.send(message);
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
        }
    }
}

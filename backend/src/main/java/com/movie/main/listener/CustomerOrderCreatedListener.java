package com.movie.main.listener;

import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.movie.main.event.CustomerOrderCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Async
@Component
@Slf4j
public class CustomerOrderCreatedListener {
    private JavaMailSender mailSender;

    public CustomerOrderCreatedListener(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @EventListener
    public void handleEvent(final CustomerOrderCreatedEvent event) {
        final var subject = "Thank you for your order";
        final var content = String.format("Dear {},%nThank you for your order",
                event.customerOrder().getCustomer().getName());

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

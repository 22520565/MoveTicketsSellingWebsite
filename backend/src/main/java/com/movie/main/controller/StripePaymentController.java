package com.movie.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.request.StripePaymentCreateIntentRequestDto;
import com.movie.main.dto.response.StripePaymentCreateIntentResponseDto;
import com.movie.main.entity.StripePayment;
import com.movie.main.resource.ResourceStrings;
import com.movie.main.service.StripePaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stripe-payments")
public class StripePaymentController {
    private final StripePaymentService service;

    public StripePaymentController(final StripePaymentService service) {
        this.service = service;
    }

    @PostMapping("/create-intent")
    public ResponseEntity<StripePaymentCreateIntentResponseDto> createIntent(
            @Valid @RequestBody final StripePaymentCreateIntentRequestDto requestDto) {
        final var result = this.service.createPaymentIntent(requestDto.amount(), requestDto.description());

        final var clientSecret = result.getValue();
        if (clientSecret != null) {
            return ResponseEntity.ok(new StripePaymentCreateIntentResponseDto(clientSecret));
        }

        return switch (result.getError()) {
        case CARD_DECLINED -> ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).build();
        default -> ResponseEntity.internalServerError().build();
        };
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestHeader("Stripe-Signature") final String sigHeader,
            @RequestBody final String payload) {
        try {
            final var event = Webhook.constructEvent(payload, sigHeader, ResourceStrings.STRIPE_WEBHOOK_SECRET);
            switch (event.getType()) {
            case "payment_intent.succeeded":
                final var intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow();
                this.service.updatePaymentStatus(intent.getId(), StripePayment.Status.from(intent.getStatus()));
                return ResponseEntity.ok().build();

            case "payment_intent.payment_failed":
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).build();

            default:
                return ResponseEntity.internalServerError().build();
            }
        }
        catch (final SignatureVerificationException exception) {
            return ResponseEntity.badRequest().build();
        }
        catch (final Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

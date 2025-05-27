package com.movie.main.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.movie.main.entity.StripePayment;
import com.movie.main.repository.StripePaymentRepository;
import com.movie.main.resource.ResourceStrings;
import com.movie.main.ulti.Expected;
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.ApiException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.IdempotencyException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.PermissionException;
import com.stripe.exception.RateLimitException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StripePaymentService {
    public enum PaymentError {
        AUTH_ERROR,
        INVALID_REQUEST,
        CARD_DECLINED,
        NETWORK_ERROR,
        SERVER_ERROR,
        RATE_LIMIT,
        IDEMPOTENCY,
        PERMISSION_DENIED,
        INTERNAL_ERROR,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    @NotNull
    private final StripePaymentRepository repository;

    public StripePaymentService(@NotNull final StripePaymentRepository repository) {
        this.repository = repository;
    }

    public Expected<String, PaymentError> createPaymentIntent(final int amount, final String description) {
        try {
            final var params = PaymentIntentCreateParams.builder()
                    .setAmount(Long.valueOf(amount))
                    .setCurrency(ResourceStrings.STRIPE_CURRENCY)
                    .setDescription(description)
                    .build();

            final var intent = PaymentIntent.create(params);

            final var stripePayment = new StripePayment(intent.getId(), StripePayment.Status.from(intent.getStatus()),
                    amount, Instant.now());
            this.repository.save(stripePayment);

            return Expected.success(intent.getClientSecret());
        }
        catch (final PermissionException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.PERMISSION_DENIED);
        }
        catch (final AuthenticationException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.AUTH_ERROR);
        }
        catch (final RateLimitException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.RATE_LIMIT);
        }
        catch (final InvalidRequestException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.INVALID_REQUEST);

        }
        catch (final CardException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.CARD_DECLINED);

        }
        catch (final ApiConnectionException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.NETWORK_ERROR);

        }
        catch (final ApiException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.SERVER_ERROR);

        }
        catch (final IdempotencyException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.IDEMPOTENCY);

        }
        catch (final StripeException exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.INTERNAL_ERROR);
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(PaymentError.UNSPECIFIED);
        }
    }

    public Expected<StripePayment, UpdateError> updatePaymentStatus(
            final String intentId,
            final StripePayment.Status status) {
        final var stripePayment = this.repository.findByPaymentIntentId(intentId).orElse(null);
        if (stripePayment == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        stripePayment.setStatus(status);

        try {
            return Expected.success(this.repository.save(stripePayment));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }
}

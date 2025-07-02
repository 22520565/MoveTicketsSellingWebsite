package com.movie.main.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.CustomerRequestDto;
import com.movie.main.dto.request.CustomerSelfRequestDto;
import com.movie.main.entity.Customer;
import com.movie.main.event.CustomerCreatedEvent;
import com.movie.main.repository.CustomerRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {
    public enum CreationError {
        USERNAME_EXISTS,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        USERNAME_EXISTS,
        UNSPECIFIED,
    }

    public enum SetPasswordResult {
        SUCCESS,
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum ResetPasswordResult {
        SUCCESS,
        ENTITY_NOT_EXISTS,
        WRONG_OLD_PASSWORD,
        UNSPECIFIED,
    }

    public enum MarkBlockedStatusResult {
        SUCCESS,
        ENTITY_NOT_EXISTS_ERROR,
        UNSPECIFIED_ERROR,
    }

    public enum MarkDeletedStatusResult {
        SUCCESS,
        ENTITY_NOT_EXISTS_ERROR,
        UNSPECIFIED_ERROR,
    }

    @NotNull
    private final CustomerRepository repository;

    @NotNull
    private final PasswordEncoder passwordEncoder;

    @NotNull
    private final UserRefreshTokenService userRefreshTokenService;

    @NotNull
    private final ApplicationEventPublisher publisher;

    public CustomerService(
            @NotNull final CustomerRepository repository,
            @NotNull final PasswordEncoder passwordEncoder,
            @NotNull final UserRefreshTokenService userRefreshTokenService,
            @NotNull final ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userRefreshTokenService = userRefreshTokenService;
        this.publisher = publisher;
    }

    @Nullable
    public Page<Customer> findAllByBlockedFalseAndDeletedFalse(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByBlockedFalseAndDeletedFalse(pageRequest);
    }

    @Nullable
    public Page<Customer> findAllByBlockedTrueAndDeletedFalse(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByBlockedTrueAndDeletedFalse(pageRequest);
    }

    @Nullable
    public Page<Customer> findAllByDeletedTrue(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedTrue(pageRequest);
    }

    @Nullable
    public Customer findByIdAndBlockedFalseAndDeletedFalse(final int id) {
        return this.repository.findByIdAndBlockedFalseAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public Customer findByIdAndBlockedTrueAndDeletedFalse(final int id) {
        return this.repository.findByIdAndBlockedTrueAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public Customer findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Nullable
    public Customer findByIdAndDeletedFalse(final int id) {
        return this.repository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public Customer findByIdAndDeletedTrue(final int id) {
        return this.repository.findByIdAndDeletedTrue(id).orElse(null);
    }

    @Nullable
    public Customer findByUsernameAndDeletedFalse(final String username) {
        return this.repository.findByUsernameAndDeletedFalse(username).orElse(null);
    }

    @Nullable
    public Customer findByUsername(final String username) {
        return this.repository.findByUsername(username).orElse(null);
    }

    public boolean existsByUsername(final String username) {
        return this.repository.existsByUsername(username);
    }

    @NotNull
    public Expected<Customer, CreationError> create(@NotNull final CustomerRequestDto requestDto) {
        if (this.existsByUsername(requestDto.username())) {
            return Expected.failure(CreationError.USERNAME_EXISTS);
        }

        final var newCustomer = new Customer(
                requestDto.name(),
                requestDto.birthDate(),
                requestDto.email(),
                requestDto.phoneNumber(),
                requestDto.username(),
                this.passwordEncoder.encode(requestDto.password()));

        try {
            final var savedCustomer = this.repository.save(newCustomer);
            this.publisher.publishEvent(new CustomerCreatedEvent(savedCustomer));

            return Expected.success(savedCustomer);
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Customer, UpdateError> updateByIdAndDeletedFalse(
            final int id,
            @NotNull final CustomerRequestDto requestDto) {
        final var customer = this.findByIdAndDeletedFalse(id);
        if (customer == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var newUsername = requestDto.username();
        if ((!Objects.equals(customer.getUsername(), newUsername)) && (this.existsByUsername(newUsername))) {
            return Expected.failure(UpdateError.USERNAME_EXISTS);
        }

        customer.setName(requestDto.name());
        customer.setBirthDate(requestDto.birthDate());
        customer.setEmail(requestDto.email());
        customer.setPhoneNumber(requestDto.phoneNumber());
        customer.setUsername(requestDto.username());
        customer.setHashedPassword(this.passwordEncoder.encode(requestDto.password()));

        try {
            return Expected.success(this.repository.save(customer));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Customer, UpdateError> updateByIdAndBlockedFalseAndDeletedFalse(
            final int id,
            @NotNull final CustomerSelfRequestDto requestDto) {
        final var customer = this.findByIdAndBlockedFalseAndDeletedFalse(id);
        if (customer == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var newUsername = requestDto.username();
        if ((!Objects.equals(customer.getUsername(), newUsername)) && (this.existsByUsername(newUsername))) {
            return Expected.failure(UpdateError.USERNAME_EXISTS);
        }

        customer.setName(requestDto.name());
        customer.setBirthDate(requestDto.birthDate());
        customer.setEmail(requestDto.email());
        customer.setPhoneNumber(requestDto.phoneNumber());
        customer.setUsername(requestDto.username());

        try {
            return Expected.success(this.repository.save(customer));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public SetPasswordResult setPasswordByIdAndDeletedFalse(
            final int id,
            final String newPassword) {
        final var customer = this.findByIdAndDeletedFalse(id);
        if (customer == null) {
            return SetPasswordResult.ENTITY_NOT_EXISTS;
        }

        customer.setHashedPassword(this.passwordEncoder.encode(newPassword));

        try {
            this.repository.save(customer);
            return SetPasswordResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return SetPasswordResult.UNSPECIFIED;
        }
    }

    @NotNull
    public ResetPasswordResult resetPasswordByIdAndBlockedFalseAndDeletedFalse(
            final int id,
            final String oldPassword,
            final String newPassword) {
        var customer = this.findByIdAndBlockedFalseAndDeletedFalse(id);
        if (customer == null)
            return ResetPasswordResult.ENTITY_NOT_EXISTS;

        if (!this.passwordEncoder.matches(oldPassword, customer.getHashedPassword())) {
            return ResetPasswordResult.WRONG_OLD_PASSWORD;
        }

        customer.setHashedPassword(this.passwordEncoder.encode(newPassword));

        try {
            this.repository.save(customer);
            return ResetPasswordResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return ResetPasswordResult.UNSPECIFIED;
        }
    }

    @NotNull
    public MarkBlockedStatusResult markAsBlockedById(final int id) {
        return this.markBlockedStatusById(id, true);
    }

    @NotNull
    public MarkBlockedStatusResult markAsUnblockedById(final int id) {
        return this.markBlockedStatusById(id, false);
    }

    @NotNull
    public MarkBlockedStatusResult markBlockedStatusById(
            final int id,
            final boolean blockedStatusToMark) {
        final var customer = this.findByIdAndDeletedFalse(id);
        if (customer == null) {
            return MarkBlockedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        customer.setBlocked(blockedStatusToMark);

        try {
            this.repository.save(customer);
            return MarkBlockedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkBlockedStatusResult.UNSPECIFIED_ERROR;
        }
    }

    @NotNull
    public MarkDeletedStatusResult markAsDeletedById(final int id) {
        return this.markDeletedStatusById(id, true);
    }

    @NotNull
    public MarkDeletedStatusResult markAsUndeletedById(final int id) {
        return this.markDeletedStatusById(id, false);
    }

    @NotNull
    public MarkDeletedStatusResult markDeletedStatusById(
            final int id,
            final boolean deletedStatusToMark) {
        final var customer = this.findById(id);
        if (customer == null) {
            return MarkDeletedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        customer.setDeleted(deletedStatusToMark);

        try {
            this.repository.save(customer);
            return MarkDeletedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkDeletedStatusResult.UNSPECIFIED_ERROR;
        }
    }
}

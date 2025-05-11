package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.CustomerRequestDto;
import com.movie.main.entity.Customer;
import com.movie.main.repository.CustomerRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService {
    public enum CreationError {
        USERNAME_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, USERNAME_EXISTS, UNSPECIFIED,
    }

    public enum MarkBlockedStatusResult {
        SUCCESS, ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum MarkDeletedStatusResult {
        SUCCESS, ENTITY_NOT_EXISTS_ERROR, UNSPECIFIED_ERROR,
    }

    @NotNull
    private final CustomerRepository repository;

    @NotNull
    private final PasswordEncoder passwordEncoder;

    @NotNull
    private final UserRefreshTokenService userRefreshTokenService;

    public CustomerService(@NotNull final CustomerRepository repository, @NotNull final PasswordEncoder passwordEncoder,
            @NotNull final UserRefreshTokenService userRefreshTokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userRefreshTokenService = userRefreshTokenService;
    }

    @Nullable
    public Page<Customer> findAllByDeletedFalse(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedFalse(pageRequest);
    }

    @Nullable
    public Page<Customer> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public Customer findByIdAndDeletedFalse(final int id) {
        return this.repository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public Customer findById(final int id) {
        return this.repository.findById(id).orElse(null);
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

        final var newCustomer = new Customer(requestDto.name(), requestDto.birthDate(), requestDto.email(),
                requestDto.phoneNumber(), requestDto.username(), this.passwordEncoder.encode(requestDto.password()));

        try {
            return Expected.success(this.repository.save(newCustomer));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Customer, UpdateError> updateByIdAndDeletedFalse(final int id,
            final CustomerRequestDto requestDto) {
        final var customer = this.findByIdAndDeletedFalse(id);
        if (customer == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var newUsername = requestDto.username();
        if ((customer.getUsername() != newUsername) && (this.existsByUsername(newUsername))) {
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
    public MarkBlockedStatusResult markAsBlockById(final int id) {
        return this.markBlockedStatusById(id, true);
    }

    @NotNull
    public MarkBlockedStatusResult markAsUnblockById(final int id) {
        return this.markBlockedStatusById(id, false);
    }

    @NotNull
    public MarkBlockedStatusResult markBlockedStatusById(final int id, final boolean blockedStatusToMark) {
        final var customer = this.findById(id);
        if (customer == null) {
            return MarkBlockedStatusResult.ENTITY_NOT_EXISTS;
        }

        customer.setBlocked(blockedStatusToMark);

        try {
            this.repository.save(customer);
            return MarkBlockedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkBlockedStatusResult.UNSPECIFIED;
        }
    }

    @NotNull
    public MarkDeletedStatusResult markAsDeletedById(final int id) {
        final var customer = this.findByIdAndDeletedFalse(id);
        if (customer == null) {
            return MarkDeletedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        customer.setDeleted(true);
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

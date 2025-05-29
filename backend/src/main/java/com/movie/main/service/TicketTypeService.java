package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.TicketTypeRequestDto;
import com.movie.main.entity.TicketType;
import com.movie.main.repository.TicketTypeRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketTypeService {
    public enum CreationError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    @NotNull
    private final TicketTypeRepository repository;

    public TicketTypeService(@NotNull final TicketTypeRepository repository) {
        this.repository = repository;
    }

    @NotNull
    public Page<@NotNull TicketType> findAll(
            @NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public TicketType findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<TicketType, CreationError> create(
            @NotNull final TicketTypeRequestDto requestDto) {
        final var newTicketType = new TicketType(requestDto.title(), requestDto.price(), requestDto.isPair());

        try {
            return Expected.success(this.repository.save(newTicketType));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<TicketType, UpdateError> updateById(
            final int id,
            @NotNull final TicketTypeRequestDto requestDto) {
        final var ticketType = this.findById(id);
        if (ticketType == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        ticketType.setTitle(requestDto.title());
        ticketType.setPrice(requestDto.price());
        ticketType.setPair(requestDto.isPair());

        try {
            return Expected.success(this.repository.save(ticketType));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public void deleteById(final int id) {
        this.repository.deleteById(id);
    }
}

package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.TheaterDTO;
import com.movie.main.entity.Theater;
import com.movie.main.repository.TheaterRepository;
import com.movie.main.service.enumclass.DeletionStatus;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public final class TheaterService {
    private final TheaterRepository repository;

    protected TheaterService(final TheaterRepository repository) {
        this.repository = repository;
    }

    @Nullable
    public Theater findById(final int id) {
        return this.repository.findById(id);
    }

    @Nullable
    public TheaterDTO findDataById(final int id) {
        return this.repository.findDataById(id);
    }

    public Theater create(@Nonnull final TheaterDTO dto) {
        try {
            final var theater = new Theater(dto.name(), dto.address());
            final var result = this.repository.add(theater);

            return result.getValue();
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    public UpdateStatus update(final int id, @Nonnull final TheaterDTO dto) {
        try {
            final var theater = this.repository.findById(id);
            if (theater == null) {
                return UpdateStatus.EntityNotExistsError;
            }

            theater.setName(dto.name());
            theater.setAddress(dto.address());

            final var result = this.repository.update(theater);
            if (result.isSuccess()) {
                return UpdateStatus.Success;
            }

            return switch (result.getError()) {
                case EntityNotExists -> UpdateStatus.EntityNotExistsError;
                case Persistence, Unspecified -> UpdateStatus.UnspecifiedError;
                default -> UpdateStatus.UnspecifiedError;
            };
        } catch (final Exception exception) {
            log.error(null, exception);
            return UpdateStatus.UnspecifiedError;
        }
    }

    public DeletionStatus deleteById(final int id) {
        try {
            return switch (this.repository.deleteById(id)) {
                case Success -> DeletionStatus.Success;
                case EntityNotExistsError -> DeletionStatus.EntityNotExistsError;
                case UnspecifiedError -> DeletionStatus.UnspecifiedError;
                default -> DeletionStatus.UnspecifiedError;
            };
        } catch (final Exception exception) {
            return DeletionStatus.UnspecifiedError;
        }
    }
}

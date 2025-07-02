package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.RoomRequestDto;
import com.movie.main.entity.Room;
import com.movie.main.repository.RoomRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoomService {
    public enum CreationError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum MarkDeletedStatusResult {
        SUCCESS,
        ENTITY_NOT_EXISTS_ERROR,
        UNSPECIFIED_ERROR,
    }

    @NotNull
    private final RoomRepository repository;

    @NotNull
    private final TheaterService theaterService;

    protected RoomService(@NotNull final RoomRepository repository, @NotNull final TheaterService theaterService) {
        this.repository = repository;
        this.theaterService = theaterService;
    }

    @NotNull
    public Page<@NotNull Room> findAllByDeletedFalse(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedFalse(pageRequest);
    }

    @NotNull
    public Page<@NotNull Room> findAllByDeletedTrue(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedTrue(pageRequest);
    }

    @Nullable
    public Room findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Nullable
    public Room findByIdAndDeletedFalse(final int id) {
        return this.repository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public Room findByIdAndDeletedTrue(final int id) {
        return this.repository.findByIdAndDeletedTrue(id).orElse(null);
    }

    @NotNull
    public Expected<Room, CreationError> create(@NotNull final RoomRequestDto requestDto) {
        final var theater = this.theaterService.findById(requestDto.theaterId());
        if (theater == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var newRoom = new Room(
                requestDto.name(),
                requestDto.numberOfSeatRow(),
                requestDto.numberOfSeatColumn(),
                requestDto.centerX1(),
                requestDto.centerX2(),
                requestDto.centerY1(),
                requestDto.centerY2(),
                requestDto.note(),
                theater);

        try {
            return Expected.success(this.repository.save(newRoom));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Room, UpdateError> updateByIdAndDeletedFalse(
            final int id,
            @NotNull final RoomRequestDto requestDto) {
        final var theater = this.theaterService.findById(requestDto.theaterId());
        if (theater == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var room = this.findByIdAndDeletedFalse(id);
        if (room == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        room.setName(requestDto.name());
        room.setNumberOfSeatRow(requestDto.numberOfSeatRow());
        room.setNumberOfSeatColumn(requestDto.numberOfSeatColumn());
        room.setCenterX1(requestDto.centerX1());
        room.setCenterX2(requestDto.centerX2());
        room.setCenterY1(requestDto.centerY1());
        room.setCenterY2(requestDto.centerY2());
        room.setNote(requestDto.note());
        room.setTheater(theater);

        try {
            return Expected.success(this.repository.save(room));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
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
        final var room = this.findById(id);
        if (room == null) {
            return MarkDeletedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        room.setDeleted(deletedStatusToMark);

        try {
            this.repository.save(room);
            return MarkDeletedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkDeletedStatusResult.UNSPECIFIED_ERROR;
        }
    }
}

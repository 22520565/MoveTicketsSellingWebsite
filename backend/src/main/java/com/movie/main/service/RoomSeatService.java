package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.RoomSeatRequestDto;
import com.movie.main.entity.RoomSeat;
import com.movie.main.repository.RoomSeatRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoomSeatService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    @NotNull
    private final RoomSeatRepository repository;

    @NotNull
    private final RoomService roomService;

    public RoomSeatService(@NotNull final RoomSeatRepository repository, @NotNull final RoomService roomService) {
        this.repository = repository;
        this.roomService = roomService;
    }

    @NotNull
    public Page<@NotNull RoomSeat> findAll(@NotNull final PageRequest pageRequest) {
        return this.repository.findAll(pageRequest);
    }

    @Nullable
    public RoomSeat findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @NotNull
    public Expected<RoomSeat, CreationError> create(@NotNull final RoomSeatRequestDto requestDto) {
        final var room = this.roomService.findById(requestDto.roomId());
        if (room == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var newRoomSeat = new RoomSeat(requestDto.name(), requestDto.type(), room);

        try {
            return Expected.success(this.repository.save(newRoomSeat));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<RoomSeat, UpdateError> updateById(final int id, @NotNull final RoomSeatRequestDto requestDto) {
        final var room = this.roomService.findById(requestDto.roomId());
        if (room == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var roomSeat = this.findById(id);
        if (roomSeat == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        roomSeat.setName(requestDto.name());
        roomSeat.setType(requestDto.type());
        roomSeat.setRoom(room);

        try {
            return Expected.success(this.repository.save(roomSeat));
        }
        catch (final Exception exception) {
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public void deleteById(final int id) {
        this.repository.deleteById(id);
    }
}

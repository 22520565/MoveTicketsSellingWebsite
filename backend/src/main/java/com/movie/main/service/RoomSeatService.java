package com.movie.main.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.RoomSeatRequestDto;
import com.movie.main.dto.response.RoomSeatResponseDto;
import com.movie.main.dto.response.RoomSeatWithUsableStatusResponseDto;
import com.movie.main.entity.RoomSeat;
import com.movie.main.repository.OrderDataFilmRepository;
import com.movie.main.repository.RoomSeatRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoomSeatService {
    public enum FetchError {
        ENTITY_NOT_EXISTS,
        ROOM_SEAT_OUT_OF_RANGE,
        UNSPECIFIED,
    }

    public enum CreationError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS,
        UNSPECIFIED,
    }

    @NotNull
    private final RoomSeatRepository repository;

    @NotNull
    private final RoomService roomService;

    @NotNull
    private final OrderDataFilmRepository orderDataFilmRepository;

    public RoomSeatService(
            @NotNull final RoomSeatRepository repository,
            @NotNull final RoomService roomService,
            @NotNull final OrderDataFilmRepository orderDataFilmRepository) {
        this.repository = repository;
        this.roomService = roomService;
        this.orderDataFilmRepository = orderDataFilmRepository;
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
    public Expected<List<List<RoomSeat>>, FetchError> getListRoomSeatsByRoomId(final int roomId) {
        try {
            final var room = this.roomService.findByIdAndDeletedFalse(roomId);
            if (room == null) {
                return Expected.failure(FetchError.ENTITY_NOT_EXISTS);
            }

            final int numRows = room.getNumberOfSeatRow();
            final int numCols = room.getNumberOfSeatColumn();

            List<List<RoomSeat>> seatMatrix = new ArrayList<>(numRows);
            for (var i = 0; i < numRows; ++i) {
                seatMatrix.add(new ArrayList<>(Collections.nCopies(numCols, null)));
            }

            final var roomSeats = this.repository.getListRoomSeatsByRoomId(roomId);
            for (final var seat : roomSeats) {
                final var name = seat.getName();
                final var rowChar = name.charAt(0);
                final var rowIdx = rowChar - 'A';

                final var colIdx = Integer.parseInt(name.substring(1)) - 1;

                if (rowIdx >= 0 && rowIdx < numRows && colIdx >= 0 && colIdx < numCols) {
                    seatMatrix.get(rowIdx).set(colIdx, seat);
                }
                else {
                    return Expected.failure(FetchError.ROOM_SEAT_OUT_OF_RANGE);
                }
            }

            return Expected.success(seatMatrix);
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(FetchError.UNSPECIFIED);
        }
    }

    @NotNull
    public Page<RoomSeatWithUsableStatusResponseDto> findByFilmShowIdAndDeletedFalse(
            final int filmShowId,
            final Pageable pageable) {
        return this.repository.findAllByFilmShowId(filmShowId, pageable).map(
                (final var roomSeat) -> {
                    final var roomSeatId = roomSeat.getId();
                    return new RoomSeatWithUsableStatusResponseDto(
                            roomSeatId,
                            roomSeat.getName(),
                            roomSeat.getType(),
                            roomSeat.getRoom().getId(),
                            this.orderDataFilmRepository.isRoomSeatUsableByFilmShowId(roomSeatId, filmShowId));
                });
    }

    @NotNull
    public Expected<RoomSeat, CreationError> create(@NotNull final RoomSeatRequestDto requestDto) {
        final var room = this.roomService.findByIdAndDeletedFalse(requestDto.roomId());
        if (room == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var newRoomSeat = new RoomSeat(
                requestDto.name(),
                requestDto.type(),
                room);

        try {
            return Expected.success(this.repository.save(newRoomSeat));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<RoomSeat, UpdateError> updateById(
            final int id,
            @NotNull final RoomSeatRequestDto requestDto) {
        final var room = this.roomService.findByIdAndDeletedFalse(requestDto.roomId());
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
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public void deleteById(final int id) {
        this.repository.deleteById(id);
    }
}

package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.RoomSeatRequestDto;
import com.movie.main.entity.RoomSeat;
import com.movie.main.repository.RoomSeatRepository;
import com.movie.main.ulti.Expected;

import jakarta.validation.constraints.NotNull;

@Service
public class RoomSeatService extends AbstractService<RoomSeatRequestDto, RoomSeat, Integer> {
    @NotNull
    private final RoomSeatRepository repository;

    @NotNull
    private final RoomService roomService;

    public RoomSeatService(@NotNull final RoomSeatRepository repository, @NotNull final RoomService roomService) {
        this.repository = repository;
        this.roomService = roomService;
    }

    @Override
    public RoomSeat create(@NotNull final RoomSeatRequestDto requestDto) {
        final var room = this.roomService.findById(requestDto.roomId());
        if (room == null) {
            return null;
        }

        final var newRoomSeat = new RoomSeat(requestDto.name(), requestDto.type(), room);

        return this.save(newRoomSeat);
    }

    @Override
    public Expected<RoomSeat, UpdateError> update(@NotNull final Integer id, @NotNull final RoomSeatRequestDto dto) {
        var roomSeat = this.findById(id);
        if (roomSeat == null) {
            return Expected.failure(UpdateError.EntityNotExists);
        }

        var room = roomSeat.getRoom();
        if (room.getId() != dto.roomId()) {
            room = this.roomService.findById(dto.roomId());
            if (room == null) {
                return Expected.failure(UpdateError.EntityNotExists);
            }
        }

        roomSeat.setName(dto.name());
        roomSeat.setType(dto.type());
        roomSeat.setRoom(room);

        roomSeat = this.save(roomSeat);
        if (roomSeat == null) {
            return Expected.failure(UpdateError.Unspecified);
        }

        return Expected.success(roomSeat);
    }

    @Override
    protected @NotNull RoomSeatRepository getRepository() {
        return this.repository;
    }

}

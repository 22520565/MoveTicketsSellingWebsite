package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.RoomSeatDto;
import com.movie.main.entity.Room;
import com.movie.main.entity.RoomSeat;
import com.movie.main.repository.RoomSeatRepository;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.validation.constraints.NotNull;

@Service
public class RoomSeatService extends AbstractService<RoomSeat, RoomSeatDto, Integer> {
    @NotNull
    private final RoomSeatRepository repository;

    @NotNull
    private final RoomService roomService;

    public RoomSeatService(@NotNull final RoomSeatRepository repository, @NotNull final RoomService roomService) {
        this.repository = repository;
        this.roomService = roomService;
    }

    @Override
    public RoomSeat create(@NotNull final RoomSeatDto dto) {
        final var room = this.roomService.findById(dto.roomId());
        if (room == null) {
            return null;
        }

        final var newSeatRoom = new RoomSeat(dto, room);
        return this.create(newSeatRoom);
    }

    @Override
    public UpdateStatus update(@NotNull final Integer id, @NotNull final RoomSeatDto dto) {
        final var roomSeat = this.findById(id);
        if (roomSeat == null) {
            return UpdateStatus.EntityNotExistsError;
        }

        var room = roomSeat.getRoom();
        if (room.getId() != dto.roomId()) {
            room = this.roomService.findById(dto.roomId());
            if (room == null) {
                return UpdateStatus.EntityNotExistsError;
            }
        }

        roomSeat.updateFromDto(dto, room);
        return this.update(roomSeat);
    }

    @Override
    protected @NotNull RoomSeatRepository getRepository() {
        return this.repository;
    }

}

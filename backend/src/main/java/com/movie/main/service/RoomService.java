package com.movie.main.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.movie.main.dto.RoomDto;
import com.movie.main.entity.Room;
import com.movie.main.entity.Theater;
import com.movie.main.repository.RoomRepository;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.validation.constraints.NotNull;

@Service
public class RoomService extends AbstractService<Room, RoomDto, Integer> {
    @NotNull
    private final RoomRepository repository;

    @NotNull
    private final TheaterService theaterService;

    protected RoomService(@NotNull final RoomRepository repository, @NotNull final TheaterService theaterService) {
        this.repository = repository;
        this.theaterService = theaterService;
    }

    @Override
    public Room create(@NotNull final RoomDto dto) {
        final var theater = this.theaterService.findById(dto.theaterId());
        if (theater == null) {
            return null;
        }

        final var newRoom = new Room(dto, theater);
        return this.create(newRoom);
    }

    @Override
    public UpdateStatus update(@NotNull final Integer id, @NotNull final RoomDto dto) {
        final var room = this.repository.findById(id);
        if (room == null) {
            return UpdateStatus.EntityNotExistsError;
        }

        var theater = room.getTheater();
        if (theater.getId() != dto.theaterId()) {
            theater = this.theaterService.findById(dto.theaterId());
            if (theater == null) {
                return UpdateStatus.EntityNotExistsError;
            }
        }

        room.updateFromDto(dto, theater);
        return this.update(room);
    }

    @Override
    protected RoomRepository getRepository() {
        return this.repository;
    }
}

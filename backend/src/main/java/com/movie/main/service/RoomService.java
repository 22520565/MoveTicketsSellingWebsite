package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.RoomDTO;
import com.movie.main.entity.Room;
import com.movie.main.repository.RoomRepository;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.annotation.Nonnull;

@Service
public class RoomService extends AbstractService<Room, RoomDTO, Integer> {
    @Nonnull
    private final RoomRepository repository;

    @Nonnull
    private final TheaterService theaterService;

    protected RoomService(@Nonnull final RoomRepository repository, @Nonnull final TheaterService theaterService) {
        this.repository = repository;
        this.theaterService = theaterService;
    }

    @Override
    protected RoomRepository getRepository() {
        return this.repository;
    }

    @Override
    public Room create(@Nonnull final RoomDTO dto) {
        final var theater = this.theaterService.findById(dto.theaterId());
        if (theater == null) {
            return null;
        }

        final var newRoom = new Room(dto.name(), theater);
        return this.create(newRoom);
    }

    @Override
    public UpdateStatus update(@Nonnull final Integer id, @Nonnull final RoomDTO dto) {
        final var newTheater = this.theaterService.findById(dto.theaterId());
        if (newTheater == null) {
            return UpdateStatus.EntityNotExistsError;
        }

        final var room = this.repository.findById(id);
        if (room == null) {
            return UpdateStatus.EntityNotExistsError;
        }

        room.setName(dto.name());
        room.setTheater(newTheater);

        return this.update(room);
    }
}

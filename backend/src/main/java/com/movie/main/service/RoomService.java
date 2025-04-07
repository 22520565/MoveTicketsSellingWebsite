package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.RoomDTO;
import com.movie.main.entity.Room;
import com.movie.main.repository.RoomRepository;
import com.movie.main.service.enumclass.DeletionStatus;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public final class RoomService {
    private final RoomRepository repository;
    private final TheaterService theaterService;

    protected RoomService(final RoomRepository repository, final TheaterService theaterService) {
        this.repository = repository;
        this.theaterService = theaterService;
    }

    @Nullable
    public Room findById(final int id) {
        return this.repository.findById(id);
    }

    @Nullable
    public RoomDTO findDataById(final int id) {
        return this.repository.findDataById(id);
    }

    public Room create(final RoomDTO dto) {
        final var theater = this.theaterService.findById(dto.theaterId());
        if (theater == null) {
            return null;
        }

        final var newRoom = new Room(dto.name(), theater);
        final var result = this.repository.add(newRoom);

        return result.getValue();
    }

    public UpdateStatus update(final int id, final RoomDTO dto) {
        try {
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

            final var result = this.repository.update(room);
            if (result.isSuccess()) {
                return UpdateStatus.Success;
            }

            return switch (result.getError()) {
                case EntityNotExists -> UpdateStatus.EntityNotExistsError;
                case Persistence, Unspecified -> UpdateStatus.UnspecifiedError;
                default -> UpdateStatus.UnspecifiedError;
            };
        } catch (final Exception exception) {
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

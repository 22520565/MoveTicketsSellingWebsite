package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.entity.Room;
import com.movie.main.repository.RoomRepository;
import com.movie.main.request.RoomCreationRequest;
import com.movie.main.request.RoomUpdateRequest;

import jakarta.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public final class RoomService {
    private final RoomRepository repository;
    private final TheaterService theaterService;

    public RoomService(final RoomRepository repository, final TheaterService theaterService) {
        this.repository = repository;
        this.theaterService = theaterService;
    }

    @Nullable
    public Room findById(final Integer id) {
        if ((id == null) || (this.repository == null)) {
            return null;
        }

        try {
            return this.repository.findById(id).orElse(null);
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    public boolean create(final RoomCreationRequest request) {
        if ((request == null) || (this.repository == null)) {
            return false;
        }

        final var theater = this.theaterService.findById(request.theaterId());
        if (theater == null) {
            return false;
        }

        final var newRoom = Room.create(request.name(), theater);
        if (newRoom == null) {
            return false;
        }

        try {
            this.repository.save(newRoom);
        } catch (final Exception exception) {
            log.error(null, exception);
            return false;
        }

        return true;
    }

    public boolean update(final Integer id, final RoomUpdateRequest request) {
        if ((id == null) || (request == null) || (this.repository == null)) {
            return false;
        }

        try {
            final var room = this.repository.findById(id).orElse(null);
            if (room == null) {
                return false;
            }

            final var newRoomName = request.name();
            if ((newRoomName == null) || (!room.setName(newRoomName))) {
                return false;
            }

            final var newTheaterId = request.theaterId();
            if (newTheaterId != null) {
                final var newTheater = this.theaterService.findById(newTheaterId);
                if (newTheater == null) {
                    return false;
                }

                room.setTheater(newTheater);
            }

            this.repository.save(room);
        } catch (final Exception exception) {
            return false;
        }

        return true;
    }

    public boolean deleteById(final Integer id) {
        if ((id == null) || (this.repository == null)) {
            return false;
        }

        try {
            this.repository.deleteById(id);
        } catch (final Exception exception) {
            log.error(null, exception);
            return false;
        }

        return true;
    }
}

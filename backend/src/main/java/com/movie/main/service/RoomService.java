package com.movie.main.service;

import java.util.Objects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.movie.main.dto.RoomRequestDto;
import com.movie.main.entity.Room;
import com.movie.main.repository.RoomRepository;
import com.movie.main.ulti.Expected;

import jakarta.validation.constraints.NotNull;

@Service
public class RoomService extends AbstractService<RoomRequestDto, Room, Integer> {
    @NotNull
    private final RoomRepository repository;

    @NotNull
    private final TheaterService theaterService;

    protected RoomService(@NotNull final RoomRepository repository, @NotNull final TheaterService theaterService) {
        this.repository = repository;
        this.theaterService = theaterService;
    }

    @Override
    public Room create(@NotNull final RoomRequestDto requestDto) {
        final var theater = this.theaterService.findById(requestDto.theaterId());
        if (theater == null) {
            return null;
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

        return this.save(newRoom);
    }

    @Override
    public Expected<Room, UpdateError> update(@NotNull final Integer id, @NotNull final RoomRequestDto dto) {
        var room = this.findById(id);
        if (room == null) {
            return Expected.failure(UpdateError.EntityNotExists);
        }

        var theater = room.getTheater();
        if (theater.getId() != dto.theaterId()) {
            theater = this.theaterService.findById(dto.theaterId());
            if (theater == null) {
                return Expected.failure(UpdateError.EntityNotExists);
            }
        }

        room.setName(dto.name());
        room.setNumberOfSeatRow(dto.numberOfSeatRow());
        room.setNumberOfSeatColumn(dto.numberOfSeatColumn());
        room.setCenterX1(dto.centerX1());
        room.setCenterX2(dto.centerX2());
        room.setCenterY1(dto.centerY1());
        room.setCenterY2(dto.centerY2());
        room.setNote(dto.note());
        room.setTheater(theater);

        room = this.save(room);
        if (room == null) {
            return Expected.failure(UpdateError.Unspecified);
        }

        return Expected.success(room);
    }

    @Override
    protected @NotNull RoomRepository getRepository() {
        return this.repository;
    }
}

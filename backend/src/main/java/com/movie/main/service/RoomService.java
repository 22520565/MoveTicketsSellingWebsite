package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.RoomRequestDto;
import com.movie.main.dto.response.RoomResponseDto;
import com.movie.main.entity.Room;
import com.movie.main.repository.RoomRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class RoomService extends AbstractEntityService<RoomRequestDto, RoomResponseDto, Room, Integer> {
    @NotNull
    private final RoomRepository repository;

    @NotNull
    private final TheaterService theaterService;

    protected RoomService(@NotNull final RoomRepository repository, @NotNull final TheaterService theaterService) {
        this.repository = repository;
        this.theaterService = theaterService;
    }

    @Override
    protected RoomResponseDto createResponseDtoFromEntity(@NotNull final Room room) {
        return new RoomResponseDto(room.getId(), room.getName(), room.getNumberOfSeatRow(),
                room.getNumberOfSeatColumn(), room.getCenterX1(), room.getCenterX2(), room.getCenterY1(),
                room.getCenterY2(), room.getNote(), room.getTheater().getId());
    }

    @Override
    protected Room createEntityFromRequestDto(@NotNull final RoomRequestDto requestDto) {
        final var theater = this.theaterService.findEntityById(requestDto.theaterId());
        if (theater == null) {
            return null;
        }

        return new Room(requestDto.name(), requestDto.numberOfSeatRow(), requestDto.numberOfSeatColumn(),
                requestDto.centerX1(), requestDto.centerX2(), requestDto.centerY1(), requestDto.centerY2(),
                requestDto.note(), theater);
    }

    @Override
    protected Room updateEntityFromRequestDto(@NotNull final Room room, @NotNull final RoomRequestDto requestDto) {
        var theater = room.getTheater();
        if (theater.getId() != requestDto.theaterId()) {
            theater = this.theaterService.findEntityById(requestDto.theaterId());
            if (theater == null) {
                return null;
            }
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

        return room;
    }

    @Override
    protected @NotNull RoomRepository getRepository() {
        return this.repository;
    }
}

package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.RoomRequestDto;
import com.movie.main.dto.response.RoomResponseDto;
import com.movie.main.entity.Room;
import com.movie.main.repository.RoomRepository;
import com.movie.main.ulti.Expected;

import jakarta.validation.constraints.NotNull;

@Service
public class RoomService extends AbstractService<RoomRequestDto, RoomResponseDto, Room, Integer> {
    @NotNull
    private final RoomRepository repository;

    @NotNull
    private final TheaterService theaterService;

    protected RoomService(
            @NotNull final RoomRepository repository,
            @NotNull final TheaterService theaterService) {
        this.repository = repository;
        this.theaterService = theaterService;
    }

    @Override
    protected RoomResponseDto createResponseDtoFromEntity(@NotNull final Room entity) {
        return new RoomResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getNumberOfSeatRow(),
                entity.getNumberOfSeatColumn(),
                entity.getCenterX1(),
                entity.getCenterX2(),
                entity.getCenterY1(),
                entity.getCenterY2(),
                entity.getNote(),
                entity.getTheater().getId());
    }

    @Override
    protected Room createEntityFromRequestDto(@NotNull final RoomRequestDto requestDto) {
        final var theater = this.theaterService.findEntityById(requestDto.theaterId());
        if (theater == null) {
            return null;
        }

        return new Room(
                requestDto.name(),
                requestDto.numberOfSeatRow(),
                requestDto.numberOfSeatColumn(),
                requestDto.centerX1(),
                requestDto.centerX2(),
                requestDto.centerY1(),
                requestDto.centerY2(),
                requestDto.note(),
                theater);
    }

    @Override
    protected Room updateEntityFromRequestDto(
            @NotNull final Room entity,
            @NotNull final RoomRequestDto requestDto) {
        var theater = entity.getTheater();
        if (theater.getId() != requestDto.theaterId()) {
            theater = this.theaterService.findEntityById(requestDto.theaterId());
            if (theater == null) {
                return null;
            }
        }

        entity.setName(requestDto.name());
        entity.setNumberOfSeatRow(requestDto.numberOfSeatRow());
        entity.setNumberOfSeatColumn(requestDto.numberOfSeatColumn());
        entity.setCenterX1(requestDto.centerX1());
        entity.setCenterX2(requestDto.centerX2());
        entity.setCenterY1(requestDto.centerY1());
        entity.setCenterY2(requestDto.centerY2());
        entity.setNote(requestDto.note());
        entity.setTheater(theater);

        return entity;
    }

    @Override
    protected @NotNull RoomRepository getRepository() {
        return this.repository;
    }
}

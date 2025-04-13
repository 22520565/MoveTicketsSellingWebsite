package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.RoomSeatRequestDto;
import com.movie.main.dto.response.RoomSeatResponseDto;
import com.movie.main.entity.RoomSeat;
import com.movie.main.repository.RoomSeatRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class RoomSeatService extends AbstractService<RoomSeatRequestDto, RoomSeatResponseDto, RoomSeat, Integer> {
    @NotNull
    private final RoomSeatRepository repository;

    @NotNull
    private final RoomService roomService;

    public RoomSeatService(
            @NotNull final RoomSeatRepository repository,
            @NotNull final RoomService roomService) {
        this.repository = repository;
        this.roomService = roomService;
    }

    @Override
    protected RoomSeatResponseDto createResponseDtoFromEntity(@NotNull final RoomSeat entity) {
        return new RoomSeatResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getRoom().getId());
    }

    @Override
    protected RoomSeat createEntityFromRequestDto(@NotNull final RoomSeatRequestDto requestDto) {
        final var room = this.roomService.findEntityById(requestDto.roomId());
        if (room == null) {
            return null;
        }

        return new RoomSeat(
                requestDto.name(),
                requestDto.type(),
                room);
    }

    @Override
    protected RoomSeat updateEntityFromRequestDto(
            @NotNull final RoomSeat entity,
            @NotNull final RoomSeatRequestDto requestDto) {
        var room = entity.getRoom();
        if (room.getId() != requestDto.roomId()) {
            room = this.roomService.findEntityById(requestDto.roomId());
            if (room == null) {
                return null;
            }
        }

        entity.setName(requestDto.name());
        entity.setType(requestDto.type());
        entity.setRoom(room);

        return entity;
    }

    @Override
    protected @NotNull RoomSeatRepository getRepository() {
        return this.repository;
    }

}

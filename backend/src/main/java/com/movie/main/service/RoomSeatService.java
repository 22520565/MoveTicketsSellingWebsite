package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.RoomSeatRequestDto;
import com.movie.main.dto.response.RoomSeatResponseDto;
import com.movie.main.entity.RoomSeat;
import com.movie.main.repository.RoomSeatRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class RoomSeatService extends AbstractEntityService<RoomSeatRequestDto, RoomSeatResponseDto, RoomSeat, Integer> {
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
    protected RoomSeatResponseDto createResponseDtoFromEntity(@NotNull final RoomSeat roomSeat) {
        return new RoomSeatResponseDto(
                roomSeat.getId(),
                roomSeat.getName(),
                roomSeat.getType(),
                roomSeat.getRoom().getId());
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
            @NotNull final RoomSeat roomSeat,
            @NotNull final RoomSeatRequestDto requestDto) {
        var room = roomSeat.getRoom();
        if (room.getId() != requestDto.roomId()) {
            room = this.roomService.findEntityById(requestDto.roomId());
            if (room == null) {
                return null;
            }
        }

        roomSeat.setName(requestDto.name());
        roomSeat.setType(requestDto.type());
        roomSeat.setRoom(room);

        return roomSeat;
    }

    @Override
    protected @NotNull RoomSeatRepository getRepository() {
        return this.repository;
    }

}

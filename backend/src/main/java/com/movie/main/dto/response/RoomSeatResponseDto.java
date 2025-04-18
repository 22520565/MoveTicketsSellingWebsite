package com.movie.main.dto.response;

public record RoomSeatResponseDto(
                Integer id,
                String name,
                String type,
                int roomId)
                implements EntityResponseDtoInterface<Integer> {
}

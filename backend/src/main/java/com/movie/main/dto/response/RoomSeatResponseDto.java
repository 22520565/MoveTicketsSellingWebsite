package com.movie.main.dto.response;

public record RoomSeatResponseDto(
        int id,
        String name,
        String type,
        int roomId) {
}

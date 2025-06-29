package com.movie.main.dto.response;

public record RoomSeatWithUsableStatusResponseDto(
        int id,
        String name,
        String type,
        int roomId,
        boolean usable) {}

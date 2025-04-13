package com.movie.main.dto.response;

public record RoomResponseDto(
        Integer id,
        String name,
        int numberOfSeatRow,
        int numberOfSeatColumn,
        int centerX1,
        int centerX2,
        int centerY1,
        int centerY2,
        String note,
        int theaterId)
        implements InterfaceResponseDto<Integer> {
}

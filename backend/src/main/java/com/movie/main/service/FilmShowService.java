package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.FilmShowRequestDto;
import com.movie.main.dto.response.FilmShowResponseDto;
import com.movie.main.entity.FilmShow;
import com.movie.main.repository.FilmShowRepository;
import jakarta.validation.constraints.NotNull;

@Service
public class FilmShowService extends AbstractService<FilmShowRequestDto, FilmShowResponseDto, FilmShow, Integer> {
    @NotNull
    private final FilmShowRepository filmShowRepository;

    @NotNull
    private final FilmService filmService;

    @NotNull
    private final RoomSeatService roomSeatService;

    public FilmShowService(
            @NotNull final FilmShowRepository filmShowRepository,
            @NotNull final FilmService filmService,
            @NotNull final RoomSeatService roomSeatService) {
        this.filmShowRepository = filmShowRepository;
        this.filmService = filmService;
        this.roomSeatService = roomSeatService;
    }

    @Override
    protected FilmShowResponseDto createResponseDtoFromEntity(@NotNull final FilmShow entity) {
        return new FilmShowResponseDto(
                entity.getId(),
                entity.getFilm().getId(),
                entity.getRoomSeat().getId(),
                entity.getShowTime(),
                entity.getType());
    }

    @Override
    protected FilmShow createEntityFromRequestDto(@NotNull final FilmShowRequestDto requestDto) {
        final var film = this.filmService.findEntityById(requestDto.filmId());
        if (film == null) {
            return null;
        }

        final var roomSeat = this.roomSeatService.findEntityById(requestDto.roomSeatId());
        if (roomSeat == null) {
            return null;
        }

        return new FilmShow(
                film,
                roomSeat,
                requestDto.showTime(),
                requestDto.type());
    }

    @Override
    protected FilmShow updateEntityFromRequestDto(
            @NotNull final FilmShow entity,
            @NotNull final FilmShowRequestDto requestDto) {
        var film = entity.getFilm();
        if (film.getId() != requestDto.filmId()) {
            film = this.filmService.findEntityById(requestDto.filmId());
            if (film == null) {
                return null;
            }
        }

        var roomSeat = entity.getRoomSeat();
        if (roomSeat.getId() != requestDto.roomSeatId()) {
            roomSeat = this.roomSeatService.findEntityById(requestDto.roomSeatId());
            if (roomSeat == null) {
                return null;
            }
        }

        entity.setFilm(film);
        entity.setRoomSeat(roomSeat);
        entity.setShowTime(requestDto.showTime());
        entity.setType(requestDto.type());

        return entity;
    }

    @Override
    protected FilmShowRepository getRepository() {
        return filmShowRepository;
    }

}

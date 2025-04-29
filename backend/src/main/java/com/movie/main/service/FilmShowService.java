package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.FilmShowRequestDto;
import com.movie.main.dto.response.FilmShowResponseDto;
import com.movie.main.entity.FilmShow;
import com.movie.main.repository.FilmShowRepository;
import jakarta.validation.constraints.NotNull;

@Service
public class FilmShowService extends AbstractEntityService<FilmShowRequestDto, FilmShowResponseDto, FilmShow, Integer> {
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
    protected FilmShowResponseDto createResponseDtoFromEntity(@NotNull final FilmShow filmShow) {
        return new FilmShowResponseDto(
                filmShow.getId(),
                filmShow.getFilm().getId(),
                filmShow.getRoomSeat().getId(),
                filmShow.getShowTime(),
                filmShow.getType());
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
            @NotNull final FilmShow filmShow,
            @NotNull final FilmShowRequestDto requestDto) {
        var film = filmShow.getFilm();
        if (film.getId() != requestDto.filmId()) {
            film = this.filmService.findEntityById(requestDto.filmId());
            if (film == null) {
                return null;
            }
        }

        var roomSeat = filmShow.getRoomSeat();
        if (roomSeat.getId() != requestDto.roomSeatId()) {
            roomSeat = this.roomSeatService.findEntityById(requestDto.roomSeatId());
            if (roomSeat == null) {
                return null;
            }
        }

        filmShow.setFilm(film);
        filmShow.setRoomSeat(roomSeat);
        filmShow.setShowTime(requestDto.showTime());
        filmShow.setType(requestDto.type());

        return filmShow;
    }

    @Override
    protected FilmShowRepository getRepository() {
        return filmShowRepository;
    }

}

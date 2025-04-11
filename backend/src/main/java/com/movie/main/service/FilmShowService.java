package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.FilmShowRequestDto;
import com.movie.main.entity.FilmShow;
import com.movie.main.repository.FilmShowRepository;
import com.movie.main.ulti.Expected;

import jakarta.validation.constraints.NotNull;

@Service
public class FilmShowService extends AbstractService<FilmShowRequestDto, FilmShow, Integer> {
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
    protected FilmShowRepository getRepository() {
        return filmShowRepository;
    }

    @Override
    public FilmShow create(@NotNull final FilmShowRequestDto requestDto) {
        final var film = this.filmService.findById(requestDto.filmId());
        if (film == null) {
            return null;
        }

        final var roomSeat = this.roomSeatService.findById(requestDto.roomSeatId());
        if (roomSeat == null) {
            return null;
        }

        final var newFilmShow = new FilmShow(
                film,
                roomSeat,
                requestDto.showTime(),
                requestDto.type());

        return this.save(newFilmShow);
    }

    @Override
    public Expected<FilmShow, UpdateError> update(
            @NotNull final Integer id,
            @NotNull final FilmShowRequestDto requestDto) {
        var filmShow = this.findById(id);
        if (filmShow == null) {
            return Expected.failure(UpdateError.EntityNotExists);
        }

        var film = filmShow.getFilm();
        if (film.getId() != requestDto.filmId()) {
            film = this.filmService.findById(requestDto.filmId());
            if (film == null) {
                return Expected.failure(UpdateError.EntityNotExists);
            }
        }

        var roomSeat = filmShow.getRoomSeat();
        if (roomSeat.getId() != requestDto.roomSeatId()) {
            roomSeat = this.roomSeatService.findById(requestDto.roomSeatId());
            if (roomSeat == null) {
                return Expected.failure(UpdateError.EntityNotExists);
            }
        }

        filmShow.setFilm(film);
        filmShow.setRoomSeat(roomSeat);
        filmShow.setShowTime(requestDto.showTime());
        filmShow.setType(requestDto.type());

        filmShow = this.save(filmShow);
        if (filmShow == null) {
            return Expected.failure(UpdateError.Unspecified);
        }

        return Expected.success(filmShow);
    }

}

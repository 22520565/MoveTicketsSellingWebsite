package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.FilmShowDto;
import com.movie.main.entity.Film;
import com.movie.main.entity.FilmShow;
import com.movie.main.entity.RoomSeat;
import com.movie.main.repository.FilmShowRepository;
import com.movie.main.service.enumclass.UpdateStatus;

import jakarta.validation.constraints.NotNull;

@Service
public class FilmShowService extends AbstractService<FilmShow, FilmShowDto, Integer> {
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
    public FilmShow create(@NotNull final FilmShowDto dto) {
        final var film = this.filmService.findById(dto.filmId());
        if (film == null) {
            return null;
        }

        final var roomSeat = this.roomSeatService.findById(dto.roomSeatId());
        if (roomSeat == null) {
            return null;
        }

        final var newFileShow = new FilmShow(dto, film, roomSeat);
        return this.create(newFileShow);
    }

    @Override
    public UpdateStatus update(@NotNull final Integer id, @NotNull final FilmShowDto dto) {
        final var filmShow = this.findById(id);
        if (filmShow == null) {
            return UpdateStatus.EntityNotExistsError;
        }

        var film = filmShow.getFilm();
        if (film.getId() != dto.filmId()) {
            film = this.filmService.findById(dto.filmId());
            if (film == null) {
                return UpdateStatus.EntityNotExistsError;
            }
        }

        var roomSeat = filmShow.getRoomSeat();
        if (roomSeat.getId() != dto.roomSeatId()) {
            roomSeat = this.roomSeatService.findById(dto.roomSeatId());
            if (roomSeat == null) {
                return UpdateStatus.EntityNotExistsError;
            }
        }

        filmShow.updateFromDto(dto, film, roomSeat);
        return this.update(filmShow);
    }

}

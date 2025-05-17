package com.movie.main.service;

import com.movie.main.dto.request.FilmShowRequestDto;
import com.movie.main.entity.Film;
import com.movie.main.entity.FilmShow;
import com.movie.main.repository.FilmShowRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FilmShowService {
    public enum CreationError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum MarkDeletedStatusResult {
        SUCCESS, ENTITY_NOT_EXISTS_ERROR, UNSPECIFIED_ERROR,
    }

    @NotNull
    private final FilmShowRepository repository;

    @NotNull
    private final FilmService filmService;

    @NotNull
    private final RoomService roomService;

    protected FilmShowService(@NotNull final FilmShowRepository repository, @NotNull final FilmService filmService,
            @NotNull final RoomService roomService) {
        this.repository = repository;
        this.filmService = filmService;
        this.roomService = roomService;
    }

    @NotNull
    public Page<@NotNull FilmShow> findAllByDeletedFalse(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedFalse(pageRequest);
    }

    @NotNull
    public Page<@NotNull FilmShow> findAllByDeletedTrue(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedTrue(pageRequest);
    }

    @NotNull
    public Page<@NotNull FilmShow> findAllByFilmIdOrderByDateTime(int filmId, @NotNull final PageRequest pageRequest) {
        return this.repository.findAllByFilmIdOrderByDateTime(filmId, pageRequest);
    }

    @NotNull
    public Page<@NotNull FilmShow> findAllByShowDateAndDeletedFalse(@NotNull final LocalDate date,
            @NotNull final PageRequest pageRequest) {
        return this.repository.findAllByShowDateAndDeletedFalse(date, pageRequest);
    }

    @NotNull
    public Page<@NotNull FilmShow> findAllByFilmIdAndShowDateAndDeletedFalse(final int filmId,
            @NotNull final LocalDate date, @NotNull final PageRequest pageRequest) {
        return this.repository.findAllByFilmIdAndShowDateAndDeletedFalse(filmId, date, pageRequest);
    }

    @NotNull
    public Page<@NotNull Film> findAllFilmsByShowDateAndDeletedFalse(@NotNull final LocalDate date,
            @NotNull final Pageable pageable) {
        return this.repository.findAllFilmsByShowDateAndDeletedFalse(date, pageable);
    }

    @NotNull
    public Page<@NotNull Film> findAllFilmsShowingFromNowToEndOfTodayAndDeletedFalse(@NotNull final Pageable pageable) {
        return this.repository.findAllFilmsShowingFromNowToEndOfTodayAndDeletedFalse(pageable);
    }

    @NotNull
    public Page<@NotNull Film> findAllFilmsShowingFromTomorrowAndDeletedFalse(int days,
            @NotNull final Pageable pageable) {
        final var tomorrowDate = LocalDate.now().plusDays(1);
        final var maxDate = LocalDate.now().plusDays(days);
        return this.repository.findAllFilmsByShowDateRangeAndDeletedFalse(tomorrowDate, maxDate, pageable);
    }

    @Nullable
    public FilmShow findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Nullable
    public FilmShow findByIdAndDeletedFalse(final int id) {
        return this.repository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public FilmShow findByIdAndDeletedTrue(final int id) {
        return this.repository.findByIdAndDeletedTrue(id).orElse(null);
    }

    @NotNull
    public Expected<FilmShow, CreationError> create(@NotNull final FilmShowRequestDto requestDto) {
        final var room = this.roomService.findByIdAndDeletedFalse(requestDto.roomId());
        if (room == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var film = this.filmService.findByIdAndDeletedFalse(requestDto.filmId());
        if (film == null) {
            return Expected.failure(CreationError.ENTITY_NOT_EXISTS);
        }

        final var newFilmShow = new FilmShow(film, room, requestDto.showDate(), requestDto.showTime(),
                requestDto.type());

        try {
            return Expected.success(this.repository.save(newFilmShow));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<FilmShow, UpdateError> updateById(final int id, @NotNull final FilmShowRequestDto requestDto) {
        final var room = this.roomService.findByIdAndDeletedFalse(requestDto.roomId());
        if (room == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var film = this.filmService.findByIdAndDeletedFalse(requestDto.filmId());
        if (film == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var filmShow = this.findByIdAndDeletedFalse(id);
        if (filmShow == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        filmShow.setFilm(film);
        filmShow.setRoom(room);
        filmShow.setShowTime(requestDto.showTime());
        filmShow.setType(requestDto.type());

        try {
            return Expected.success(this.repository.save(filmShow));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public MarkDeletedStatusResult markAsDeletedById(final int id) {
        return this.markDeletedStatusById(id, true);
    }

    @NotNull
    public MarkDeletedStatusResult markAsUndeletedById(final int id) {
        return this.markDeletedStatusById(id, false);
    }

    @NotNull
    public MarkDeletedStatusResult markDeletedStatusById(final int id, final boolean deletedStatusToMark) {
        final var filmShow = this.findById(id);
        if (filmShow == null) {
            return MarkDeletedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        filmShow.setDeleted(deletedStatusToMark);

        try {
            this.repository.save(filmShow);
            return MarkDeletedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkDeletedStatusResult.UNSPECIFIED_ERROR;
        }
    }
}

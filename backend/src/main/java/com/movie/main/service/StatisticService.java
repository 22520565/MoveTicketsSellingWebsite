package com.movie.main.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.movie.main.dto.internal.FilmShowEvent;
import com.movie.main.dto.internal.RevenueByMonth;
import com.movie.main.dto.response.BestSellingItemResponseDto;
import com.movie.main.dto.response.DailyStatisticResponseDto;
import com.movie.main.dto.response.FilmStatisticsResponseDto;
import com.movie.main.dto.response.HotFilmResponseDto;
import com.movie.main.dto.response.ItemRevenueResponseDto;
import com.movie.main.dto.response.MonthlyStatisticResponseDto;
import com.movie.main.dto.response.TicketCategoryRevenueResponseDto;
import com.movie.main.dto.response.TicketRateOfFilmResponseDto;
import com.movie.main.dto.response.TicketServeRateResponseDto;
import com.movie.main.entity.Tag;
import com.movie.main.repository.CustomerOrderRepository;
import com.movie.main.repository.FilmRepository;
import com.movie.main.repository.FilmShowRepository;
import com.movie.main.repository.OrderDataFilmRepository;
import com.movie.main.repository.OrderDataItemRepository;
import com.movie.main.repository.RoomRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class StatisticService {
    @NotNull
    private final CustomerOrderRepository customerOrderRepository;

    @NotNull
    private final OrderDataFilmRepository orderDataFilmRepository;

    @NotNull
    private final OrderDataItemRepository orderDataItemRepository;

    @NotNull
    private final FilmShowRepository filmShowRepository;

    @NotNull
    private final FilmRepository filmRepository;

    @NotNull
    private final RoomRepository roomRepository;

    public StatisticService(
            @NotNull final CustomerOrderRepository customerOrderRepository,
            @NotNull final OrderDataFilmRepository orderDataFilmRepository,
            @NotNull final OrderDataItemRepository orderDataItemRepository,
            @NotNull final FilmShowRepository filmShowRepository,
            @NotNull final FilmRepository filmRepository,
            @NotNull final RoomRepository roomRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.orderDataFilmRepository = orderDataFilmRepository;
        this.orderDataItemRepository = orderDataItemRepository;
        this.filmShowRepository = filmShowRepository;
        this.filmRepository = filmRepository;
        this.roomRepository = roomRepository;
    }

    @NotNull
    public DailyStatisticResponseDto getDailyStatisticByDate(final LocalDate date) {
        final long totalNetRevenue = Objects.requireNonNullElse(
                this.customerOrderRepository.getTotalNetRevenueByDate(date), 0L);

        final long totalEffectiveRevenue = Objects.requireNonNullElse(
                this.customerOrderRepository.getTotalEffectiveRevenueByDate(date), 0L);

        final long totalTicketRevenue = Objects.requireNonNullElse(
                this.orderDataFilmRepository.getTotalTicketRevenueByDate(date), 0L);

        final long totalItemRevenue = Objects.requireNonNullElse(
                this.orderDataItemRepository.getTotalItemRevenueByDate(date), 0L);

        return new DailyStatisticResponseDto(
                totalNetRevenue,
                totalEffectiveRevenue,
                totalTicketRevenue,
                totalItemRevenue);
    }

    @NotNull
    public DailyStatisticResponseDto getDailyStatisticByDateAndTheaterId(
            final LocalDate date,
            final int theaterId) {
        final long totalNetRevenue = Objects.requireNonNullElse(
                this.customerOrderRepository.getTotalNetRevenueByDateAndTheaterId(date, theaterId), 0L);

        final var totalEffectiveRevenue = Objects.requireNonNullElse(
                this.customerOrderRepository.getTotalEffectiveRevenueByDateAndTheaterId(date, theaterId), 0L);

        final var totalTicketRevenue = Objects.requireNonNullElse(
                this.orderDataFilmRepository.getTotalTicketRevenueByDateAndTheaterId(date, theaterId), 0L);

        final var totalItemRevenue = Objects.requireNonNullElse(
                this.orderDataItemRepository.getTotalItemRevenueByDateAndTheaterId(date, theaterId), 0L);

        return new DailyStatisticResponseDto(
                totalNetRevenue,
                totalEffectiveRevenue,
                totalTicketRevenue,
                totalItemRevenue);
    }

    public List<MonthlyStatisticResponseDto> getMonthlyStatisticByYear(final int year) {
        final var monthlyNetRevenue = this.customerOrderRepository
                .getMonthlyNetRevenueByYear(year);

        final var monthlyEffectiveRevenue = this.customerOrderRepository
                .getMonthlyEffectiveRevenueByYear(year);

        final var effectiveMap = monthlyEffectiveRevenue.stream()
                .collect(Collectors.toMap(
                        RevenueByMonth::month,
                        RevenueByMonth::totalRevenue));

        return monthlyNetRevenue.stream()
                .map((final var net) -> {
                    final var effective = effectiveMap.getOrDefault(net.month(), 0L);
                    return new MonthlyStatisticResponseDto(
                            net.month(),
                            net.totalRevenue(),
                            effective);
                })
                .toList();
    }

    public List<MonthlyStatisticResponseDto> getMonthlyStatisticByYearAndTheaterId(
            final int year,
            final int theaterId) {
        final var monthlyNetRevenue = this.customerOrderRepository
                .getMonthlyNetRevenueByYearAndTheaterId(year, theaterId);
        final var monthlyEffectiveRevenue = this.customerOrderRepository
                .getMonthlyEffectiveRevenueByYearAndTheaterId(year, theaterId);

        final var effectiveMap = monthlyEffectiveRevenue.stream()
                .collect(Collectors.toMap(
                        RevenueByMonth::month,
                        RevenueByMonth::totalRevenue));

        return monthlyNetRevenue.stream()
                .map((final var net) -> {
                    final var effective = effectiveMap.getOrDefault(net.month(), 0L);
                    return new MonthlyStatisticResponseDto(
                            net.month(),
                            net.totalRevenue(),
                            effective);
                })
                .toList();
    }

    public FilmStatisticsResponseDto getFilmStatisticByDate(final LocalDate date, final Pageable pageable) {
        final var filmShows = filmShowRepository.findByShowDateAndDeletedFalse(date, pageable);
        final var filmShowsSize = filmShows.getSize();

        final HashSet<String> roomNames = HashSet.newHashSet(filmShowsSize);
        final var events = new ArrayList<FilmShowEvent>(filmShowsSize);
        var index = pageable.getOffset();

        for (final var filmShow : filmShows.getContent()) {
            final var roomId = filmShow.getRoom().getId();
            final var filmId = filmShow.getFilm().getId();

            final var room = this.roomRepository.findById(roomId).orElse(null);
            if (room == null) {
                continue;
            }

            final var film = this.filmRepository.findById(filmId).orElse(null);
            if (film == null) {
                continue;
            }

            roomNames.add(room.getName());

            final var categoryNames = film.getTags().stream()
                    .map(Tag::getName)
                    .toList();

            ++index;
            final var event = new FilmShowEvent(
                    index,
                    room.getName(),
                    film.getName(),
                    filmShow.getShowTime(),
                    film.getDuration(),
                    categoryNames,
                    filmShow.getShowDate(),
                    film.getDescription());

            events.add(event);
        }

        return new FilmStatisticsResponseDto(
                roomNames,
                events);
    }

    public FilmStatisticsResponseDto getFilmStatisticByDateAndTheaterId(
            final LocalDate date,
            final int theaterId,
            final Pageable pageable) {
        final var filmShows = filmShowRepository.findByShowDateAndTheaterIdAndDeletedFalse(date, theaterId, pageable);
        final var filmShowsSize = filmShows.getSize();

        final HashSet<String> roomNames = HashSet.newHashSet(filmShowsSize);
        final var events = new ArrayList<FilmShowEvent>(filmShowsSize);
        var index = pageable.getOffset();

        for (final var filmShow : filmShows.getContent()) {
            final var roomId = filmShow.getRoom().getId();
            final var filmId = filmShow.getFilm().getId();

            final var room = this.roomRepository.findById(roomId).orElse(null);
            if (room == null) {
                continue;
            }

            final var film = this.filmRepository.findById(filmId).orElse(null);
            if (film == null) {
                continue;
            }

            roomNames.add(room.getName());

            final var categoryNames = film.getTags().stream()
                    .map(Tag::getName)
                    .toList();

            ++index;
            final var event = new FilmShowEvent(
                    index,
                    room.getName(),
                    film.getName(),
                    filmShow.getShowTime(),
                    film.getDuration(),
                    categoryNames,
                    filmShow.getShowDate(),
                    film.getDescription());

            events.add(event);
        }

        return new FilmStatisticsResponseDto(
                roomNames,
                events);
    }

    public HotFilmResponseDto getHotFilmByDate(final LocalDate date) {
        return this.orderDataFilmRepository.getHotFilmOfDay(date);
    }

    public HotFilmResponseDto getHotFilmByDateAndTheaterId(final LocalDate date, final int theaterId) {
        return this.orderDataFilmRepository.getHotFilmOfDayByTheaterId(date, theaterId);
    }

    public BestSellingItemResponseDto getBestSellingItemByDate(final LocalDate date) {
        return this.orderDataItemRepository.getBestSellingItemByDay(date);
    }

    public BestSellingItemResponseDto getBestSellingItemByDateAndTheaterId(
            final LocalDate date,
            final int theaterId) {
        return this.orderDataItemRepository.getBestSellingItemByDayAndTheaterId(date, theaterId);
    }

    public TicketServeRateResponseDto getTicketServeRateByDate(final LocalDate date) {
        return this.customerOrderRepository.getTicketServeRateByDate(date);
    }

    public TicketServeRateResponseDto getTicketServeRateByDateAndTheaterId(
            final LocalDate date, final int theaterId) {
        return this.customerOrderRepository.getTicketServeRateByDateAndTheaterId(date, theaterId);
    }

    public Page<TicketCategoryRevenueResponseDto> getTicketCategoryRevenueByDate(
            final LocalDate date,
            @NotNull final Pageable pageable) {
        return this.customerOrderRepository.getTicketCategoryRevenueByDate(date, pageable);
    }

    public Page<TicketCategoryRevenueResponseDto> getTicketCategoryRevenueByDateAndTheaterId(
            final LocalDate date,
            final int theaterId,
            @NotNull final Pageable pageable) {
        return this.customerOrderRepository.getTicketCategoryRevenueByDateAndTheaterId(date, theaterId, pageable);
    }

    public Page<ItemRevenueResponseDto> getAdditionalItemsRevenueByDate(
            final LocalDate date,
            @NotNull final Pageable pageable) {
        return this.customerOrderRepository.getAdditionalItemsRevenueByDate(date, pageable);
    }

    public Page<ItemRevenueResponseDto> getAdditionalItemsRevenueByDateAndTheaterId(
            final LocalDate date,
            final int theaterId,
            @NotNull final Pageable pageable) {
        return this.customerOrderRepository.getAdditionalItemsRevenueByDateAndTheaterId(date, theaterId, pageable);
    }

    public Page<TicketRateOfFilmResponseDto> getTicketRateOfFilmByDate(
            final LocalDate date,
            @NotNull final Pageable pageable) {
        return this.customerOrderRepository.getTicketRateOfFilmByDate(date, pageable);
    }

    public Page<TicketRateOfFilmResponseDto> getTicketRateOfFilmByDateAndTheaterId(
            final LocalDate date,
            final int theaterId,
            @NotNull final Pageable pageable) {
        return this.customerOrderRepository.getTicketRateOfFilmByDateAndTheaterId(date, theaterId, pageable);
    }
}

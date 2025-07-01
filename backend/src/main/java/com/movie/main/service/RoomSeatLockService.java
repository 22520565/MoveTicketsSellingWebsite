package com.movie.main.service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.movie.main.entity.RoomSeatLock;
import com.movie.main.repository.RoomSeatLockRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoomSeatLockService {
    @NotNull
    private final RoomSeatLockRepository repository;

    public RoomSeatLockService(
            @NotNull final RoomSeatLockRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Scheduled(fixedRate = RoomSeatLock.AmountValidMinutes, timeUnit = TimeUnit.MINUTES)
    public void clearExpiredLocks() {
        this.repository.deleteByExpireAtBefore(LocalDateTime.now());
    }
}

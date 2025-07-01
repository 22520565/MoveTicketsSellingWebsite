package com.movie.main.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.RoomSeatLock;

import jakarta.transaction.Transactional;

@Repository
public interface RoomSeatLockRepository extends JpaRepository<RoomSeatLock, Integer> {
    @Transactional
    @Modifying(clearAutomatically = true)
    void deleteByExpireAtBefore(final LocalDateTime dateTime);

    @Transactional
    @Modifying(clearAutomatically = true)
    void deleteBySessionId(final String sessionId);
}

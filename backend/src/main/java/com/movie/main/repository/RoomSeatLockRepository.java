package com.movie.main.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.RoomSeatLock;

@Repository
public interface RoomSeatLockRepository extends JpaRepository<RoomSeatLock, Integer> {
    void deleteByExpireAtBefore(final LocalDateTime dateTime);

    void deleteBySessionId(final String sessionId);
}

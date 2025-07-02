package com.movie.main.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Room;

import jakarta.validation.constraints.NotNull;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    @NotNull
    Optional<Room> findByIdAndDeletedFalse(final int id);

    @NotNull
    Optional<Room> findByIdAndDeletedTrue(final int id);

    @NonNull
    Page<@NotNull Room> findAllByDeletedFalse(@NonNull final Pageable pageable);

    @NonNull
    Page<@NotNull Room> findAllByDeletedTrue(@NonNull final Pageable pageable);
}

package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.entity.User;

@Repository
public interface UserRepository extends InterfaceRepository<User, Integer> {
    User findByUsername(final String username);
}

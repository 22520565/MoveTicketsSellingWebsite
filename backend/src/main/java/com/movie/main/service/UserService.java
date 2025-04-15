package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(final UserRepository repository) {
        this.repository = repository;
    }
}

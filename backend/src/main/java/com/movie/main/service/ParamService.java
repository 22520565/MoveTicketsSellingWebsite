package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.entity.Param;
import com.movie.main.repository.ParamRepository;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ParamService {
    private final ParamRepository repository;

    public ParamService(final ParamRepository repository) {
        this.repository = repository;
    }

    @Nullable
    public Param getParam() {
        final var param = this.repository.findById(Param.DefaultId).orElse(null);
        if (param != null) {
            return param;
        }

        try {
            return this.repository.save(new Param());
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    @Nullable
    public Param updateParam(final Param newParam) {
        try {
            return this.repository.save(newParam);
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }
}

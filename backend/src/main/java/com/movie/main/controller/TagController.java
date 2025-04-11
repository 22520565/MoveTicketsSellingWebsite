package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.TagDto;
import com.movie.main.entity.Tag;
import com.movie.main.service.AbstractService;
import com.movie.main.service.TagService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("tags")
public class TagController extends AbstractController<Tag, TagDto, Integer> {
    @NotNull
    private TagService service;

    protected TagController(@NotNull TagService service) {
        this.service = service;
    }

    @Override
    protected AbstractService<Tag, TagDto, Integer> getService() {
        return this.service;
    }

}

package com.movie.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.dto.TagRequestDto;
import com.movie.main.entity.Tag;
import com.movie.main.service.TagService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("tags")
public class TagController extends AbstractController<TagRequestDto, Tag, Integer> {
    @NotNull
    private TagService service;

    protected TagController(@NotNull TagService service) {
        this.service = service;
    }

    @Override
    protected TagService getService() {
        return this.service;
    }

}

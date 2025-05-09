package com.movie.main.controller;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.movie.main.dto.request.EntityRequestDtoInterface;
import com.movie.main.dto.response.EntityResponseDtoInterface;
import com.movie.main.entity.Identifiable;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public abstract class AbstractPublicFindEntityController<TRequestDto extends EntityRequestDtoInterface,
        TResponseDto extends EntityResponseDtoInterface<TKey>,
        TEntity extends Identifiable<TKey>,
        TKey> extends AbstractEntityController<TRequestDto, TResponseDto, TEntity, TKey> {
    @Override
    @PermitAll
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<TResponseDto>>> findAllData(
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER_STRING) @Min(value = 0) @Valid final int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE_STRING) @Range(min = 1, max = MAX_PAGE_SIZE) @Valid final int size,
            final PagedResourcesAssembler<TResponseDto> assembler) {
        return super.findAllData(page, size, assembler);
    }

    @Override
    @PermitAll
    @GetMapping("{id}")
    public ResponseEntity<TResponseDto> findById(@PathVariable @NotNull @Valid final TKey id) {
        return super.findById(id);
    }
}

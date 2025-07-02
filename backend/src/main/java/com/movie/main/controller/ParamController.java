package com.movie.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.main.auth.RequirePermission;
import com.movie.main.config.OpenApiConfig;
import com.movie.main.dto.request.ParamRequestDto;
import com.movie.main.dto.response.ParamResponseDto;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.entity.Param;
import com.movie.main.service.ParamService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/params")
@RequirePermission(value = Permission.ADMIN)
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class ParamController {
    private final ParamService service;

    public ParamController(final ParamService service) {
        this.service = service;
    }

    @GetMapping
    @PermitAll
    public ResponseEntity<ParamResponseDto> getParam() {
        final var result = this.service.getParam();
        if (result != null) {
            return ResponseEntity.ok(ParamController.getResponseDtoFrom(result));
        }

        return ResponseEntity.internalServerError().build();
    }

    @PutMapping
    public ResponseEntity<ParamResponseDto> updateParam(@RequestBody @Valid final ParamRequestDto requestDto) {
        final var param = new Param(
                requestDto.loyalPointOrderToPointRatio(),
                requestDto.loyalPointPointToReducedPriceRatio(),
                requestDto.loyalPointMinimumValueToUseLoyalPoint(),
                requestDto.loyalPointMaximumPointUseInOneGo(),
                requestDto.maximumDiscountRate(),
                requestDto.addedPriceForVipSeat());

        final var result = this.service.updateParam(param);
        if (result != null) {
            return ResponseEntity.ok(ParamController.getResponseDtoFrom(result));
        }

        return ResponseEntity.internalServerError().build();
    }

    @NotNull
    public static ParamResponseDto getResponseDtoFrom(@NotNull final Param param) {
        return new ParamResponseDto(
                param.getLoyalPointOrderToPointRatio(),
                param.getLoyalPointPointToReducedPriceRatio(),
                param.getLoyalPointMinimumValueToUseLoyalPoint(),
                param.getLoyalPointMaximumPointUseInOneGo(),
                param.getMaximumDiscountRate(),
                param.getAddedPriceForVipSeat());
    }
}

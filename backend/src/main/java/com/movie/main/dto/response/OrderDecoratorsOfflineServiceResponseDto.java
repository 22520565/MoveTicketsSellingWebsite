package com.movie.main.dto.response;

public record OrderDecoratorsOfflineServiceResponseDto(int customerOrderId, boolean printed, boolean served,
        String invalidReasonPrinted, String invalidReasonServed) {}

package com.movie.main.dto.request;

public record OrderDecoratorsOfflineServiceRequestDto(
        int customerOrderId,
        boolean printed,
        boolean served,
        String invalidReasonPrinted,
        String invalidReasonServed) {
}

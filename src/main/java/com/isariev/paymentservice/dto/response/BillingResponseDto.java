package com.isariev.paymentservice.dto.response;

public record BillingResponseDto(
        int amount,

        String currency
) {
}

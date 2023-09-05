package com.isariev.paymentservice.dto.payoutResponse;

public record PayoutBillingResponseDto(
        String currency,

        int amount
) {
}

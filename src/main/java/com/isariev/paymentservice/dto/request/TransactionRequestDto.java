package com.isariev.paymentservice.dto.request;

import jakarta.validation.constraints.NotNull;

public record TransactionRequestDto(
        @NotNull
        Integer amount,

        @NotNull
        String currency,

        String notification_url,

        String return_url,

        @NotNull
        CustomerRequestDto customer,

        Boolean test_mode
) {
}

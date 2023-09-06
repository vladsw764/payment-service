package com.isariev.paymentservice.dto.response;

import lombok.Builder;

@Builder
public record TransactionResponseDto(
        String uid,

        String type,

        String status,

        String message,

        int amount,

        String currency,

        String created_at,

        String updated_at,

        String yoda_wallet_transaction_id,

        CustomerResponseDto customer,

        FormResponseDto form,

        BillingResponseDto billing,

        boolean test_mode
) {
}

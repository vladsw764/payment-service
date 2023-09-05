package com.isariev.paymentservice.dto.payoutResponse;

public record PayoutResponseDto(
        String uid,

        String type,

        String status,

        String message,

        int amount,

        String currency,

        String created_at,

        String updated_at,

        String yoda_wallet_transaction_id,

        PayoutCustomerResponseDto customer,

        PayoutFormResponseDto form,

        PayoutBillingResponseDto billing,

        String notification_url,

        String return_url
) {
}

package com.isariev.paymentservice.dto.payoutResponse;

public record PayoutCustomerResponseDto(
        String first_name,

        String last_name,

        String country,

        String city,

        String zip,

        String address,

        String phone_number,

        String device_id,

        String unique_customer_identifier,

        String account_creation_date,

        String account_creation_country,

        String email
) {
}

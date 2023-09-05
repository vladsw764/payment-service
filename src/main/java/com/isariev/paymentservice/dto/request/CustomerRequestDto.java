package com.isariev.paymentservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequestDto(
        @NotNull @Size(max = 32)
        @Pattern(regexp = "^[A-Za-z]*(([,.'] |[ '-])[A-Za-z][a-z]*)*(\\.?)$")
        String first_name,

        @NotNull
        @Pattern(regexp = "^[A-Za-z]*(([,.'] |[ '-])[A-Za-z][a-z]*)*(\\.?)$")
        String last_name,

        @NotNull
        @Email
        String email,

        String country,

        @Pattern(regexp = "^[0-9\\-+]{7,15}$")
        String phone_number
) {
}

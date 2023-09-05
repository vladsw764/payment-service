package com.isariev.paymentservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "customers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    private UUID uid;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    private String email;

    private String country;

    private String city;

    private String zip;

    private String address;

    @Column("phone_number")
    private String phoneNumber;

    @Column("device_id")
    private String deviceId;

    @CreatedDate
    @Column("account_creation_date")
    private LocalDateTime accountCreationDate;

    @Column("account_creation_country")
    private String accountCreationCountry;
}

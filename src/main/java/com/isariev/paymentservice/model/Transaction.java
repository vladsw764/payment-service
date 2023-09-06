package com.isariev.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "transactions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    private UUID uid;

    private String type;

    private String status;

    private String message;

    private int amount;

    private String currency;

    @Column("notification_url")
    private String notificationUrl;

    @Column("return_url")
    private String returnUrl;

    @Column("test_mode")
    private boolean testMode;

    @Column("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column("updated_at")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Column("yoda_wallet_transaction_id")
    private UUID yodaWalletTransactionId;

    @Column("customer_id")
    private UUID customerId;

    @Transient
    private Customer customer;

}

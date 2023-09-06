package com.isariev.paymentservice.dto.mapper;

import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.BillingResponseDto;
import com.isariev.paymentservice.dto.response.FormResponseDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import com.isariev.paymentservice.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransactionMapper {

    private final CustomerMapper customerMapper;

    public TransactionMapper(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }


    public Transaction toEntity(TransactionRequestDto requestDto) {
        return Transaction.builder()
                .status("PENDING")
                .message("Transaction in processing")
                .amount(requestDto.amount())
                .currency(requestDto.currency())
                .notificationUrl(requestDto.notification_url())
                .returnUrl(requestDto.return_url())
                .yodaWalletTransactionId(UUID.randomUUID())
                .testMode(requestDto.test_mode())
                .build();
    }

    public TransactionResponseDto toResponseDto(Transaction transaction, FormResponseDto formResponseDto) {
        return new TransactionResponseDto(
                transaction.getUid().toString(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getMessage(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getCreatedAt() == null ? null : transaction.getCreatedAt().toString(),
                transaction.getUpdatedAt() == null ? null : transaction.getUpdatedAt().toString(),
                transaction.getYodaWalletTransactionId().toString(),
                customerMapper.mapToResponseDto(transaction.getCustomer()),
                formResponseDto,
                new BillingResponseDto(transaction.getAmount(), transaction.getCurrency()),
                true
        );
    }
}

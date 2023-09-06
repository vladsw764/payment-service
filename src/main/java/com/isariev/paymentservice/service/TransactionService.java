package com.isariev.paymentservice.service;

import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import com.isariev.paymentservice.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransactionService {
    Mono<TransactionResponseDto> createTransaction(TransactionRequestDto transactionRequest);

    Mono<String> getTransactionStatus(UUID uid);

    Mono<TransactionResponseDto> getById(UUID id, String requestURI, String method);

    Flux<TransactionResponseDto> getAllTransactions(String requestURI, String method);

    Mono<Void> receiveTransactionStatus(Transaction transaction);
}

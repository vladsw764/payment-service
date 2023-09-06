package com.isariev.paymentservice.service;

import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import com.isariev.paymentservice.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<TransactionResponseDto> createTransaction(TransactionRequestDto transactionRequest, String requestURI, String method);

    Flux<TransactionResponseDto> getAllTransactions(String requestURI, String method);

    Mono<TransactionResponseDto> getById(String id, String requestURI, String method);

    Mono<TransactionResponseDto> createPayout(TransactionRequestDto transactionRequest, String requestURI, String method);

    Mono<String> getTransactionStatus(String uid);

    Mono<Void> receiveTransactionStatus(Transaction transaction);
}

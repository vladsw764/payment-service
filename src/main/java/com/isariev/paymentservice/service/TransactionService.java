package com.isariev.paymentservice.service;

import com.isariev.paymentservice.dto.payoutResponse.PayoutResponseDto;
import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<TransactionResponseDto> createPaymentTransaction(TransactionRequestDto transactionRequest, String requestURI, String method);

    Mono<PayoutResponseDto> getPayoutTransactionById(String id, String requestURI, String method);

    Flux<TransactionResponseDto> getAllTransactions(String requestURI, String method);

    Mono<TransactionResponseDto> getPaymentTransactionById(String id, String requestURI, String method);

    Mono<PayoutResponseDto> createPayoutTransaction(TransactionRequestDto transactionRequest, String requestURI, String method);

    Flux<PayoutResponseDto> getAllPayoutTransactions(String requestURI, String method);
}

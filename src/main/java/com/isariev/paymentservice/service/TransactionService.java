package com.isariev.paymentservice.service;

import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<TransactionResponseDto> createTransaction(TransactionRequestDto transactionRequest);
}

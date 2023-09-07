package com.isariev.paymentservice.service;

import com.isariev.paymentservice.model.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionStatusService {
    Mono<String> getTransactionStatus(String id);

    Mono<Void> receiveTransactionStatus(Transaction transaction);
}

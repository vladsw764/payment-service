package com.isariev.paymentservice.repository;

import com.isariev.paymentservice.model.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, UUID> {
    @Query("SELECT status FROM transactions WHERE uid = :uid")
    Mono<String> getStatusById(UUID uid);

    Flux<Transaction> findByStatus(String status);

    @Query("UPDATE transactions SET status = :status WHERE uid = :uid")
    Mono<Void> updateTransactionStatus(String status, UUID uid);
}

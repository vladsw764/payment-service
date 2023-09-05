package com.isariev.paymentservice.repository;

import com.isariev.paymentservice.model.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, UUID> {
}

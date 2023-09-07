package com.isariev.paymentservice.service;

import com.isariev.paymentservice.exception.ConvertIdException;
import com.isariev.paymentservice.model.Transaction;
import com.isariev.paymentservice.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class TransactionStatusServiceImpl implements TransactionStatusService {

    private final TransactionRepository transactionRepository;
    private final WebhookService webhookService;

    public TransactionStatusServiceImpl(TransactionRepository transactionRepository, WebhookService webhookService) {
        this.transactionRepository = transactionRepository;
        this.webhookService = webhookService;
    }

    /**
     * Retrieves the status of a transaction by its unique identifier.
     *
     * @param id The unique identifier of the transaction.
     * @return A Mono<String> representing the transaction status.
     */
    @Override
    public Mono<String> getTransactionStatus(String id) {
        return transactionRepository.getStatusById(getUuid(id));
    }

    /**
     * Receives and logs the status of a transaction.
     *
     * @param transaction The Transaction object containing the status information.
     * @return A Mono<Void> indicating the completion of the operation.
     */
    @Override
    public Mono<Void> receiveTransactionStatus(Transaction transaction) {
        log.info(transaction.getStatus());
        return Mono.empty();
    }

    /**
     * Emulates the automatic processing of pending transactions by changing their status.
     * This method runs on a scheduled basis and has a 50% probability of setting the status to "FAILED" or "SUCCESS" for each pending transaction.
     */
    @Scheduled(fixedDelay = 10000) //Change the status every 100 seconds with a 50% probability.
    public void autoRefreshTransactionStatus() {
        Flux<Transaction> pendingTransactions = transactionRepository.findByStatus("PENDING");
        pendingTransactions.flatMap(transaction -> {
            String status = Math.random() > 0.5 ? "FAILED" : "SUCCESS";
            transaction.setStatus(status);
            return webhookService.sendTransactionWebhook(transaction)
                    .then(transactionRepository.updateTransactionStatus(status, transaction.getUid()));
        }).subscribe();
    }

    private UUID getUuid(String id) {
        UUID uid;
        try {
            uid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ConvertIdException(e.getMessage());
        }
        return uid;
    }
}

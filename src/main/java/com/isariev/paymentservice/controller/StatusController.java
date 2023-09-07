package com.isariev.paymentservice.controller;

import com.isariev.paymentservice.model.Transaction;
import com.isariev.paymentservice.service.TransactionStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controller for managing transaction statuses.
 */
@RestController
@RequestMapping("v1/status")
public class StatusController {

    private final TransactionStatusService transactionService;

    public StatusController(TransactionStatusService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Retrieve the status of a transaction by its UID.
     *
     * @param uid The UID of the transaction to retrieve the status for.
     * @return A ResponseEntity containing the transaction status as a Mono<String>.
     */
    @GetMapping("/{uid}")
    public ResponseEntity<Mono<String>> getTransactionStatusById(@PathVariable(name = "uid") String uid) {
        return ResponseEntity.ok(transactionService.getTransactionStatus(uid));
    }

    /**
     * Receive and handle the status of a transaction.
     *
     * @param transaction The Transaction object representing the transaction status to receive.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> receiveTransactionStatus(@RequestBody Transaction transaction) {
        return transactionService.receiveTransactionStatus(transaction);
    }
}

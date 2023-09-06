package com.isariev.paymentservice.controller;

import com.isariev.paymentservice.model.Transaction;
import com.isariev.paymentservice.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("v1/status")
public class StatusController {

    private final TransactionService transactionService;

    public StatusController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{uid}")
    public ResponseEntity<Mono<String>> getTransactionStatusById(@PathVariable(name = "uid") String uid) {
        return ResponseEntity.ok(transactionService.getTransactionStatus(uid));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> receiveTransactionStatus(@RequestBody Transaction transaction) {
        return transactionService.receiveTransactionStatus(transaction);
    }
}

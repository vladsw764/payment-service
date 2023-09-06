package com.isariev.paymentservice.controller;

import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import com.isariev.paymentservice.model.Transaction;
import com.isariev.paymentservice.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Mono<TransactionResponseDto>> createTransaction(@RequestBody TransactionRequestDto transactionRequest) {
        return new ResponseEntity<>(transactionService.createTransaction(transactionRequest), HttpStatus.CREATED);
    }

    @GetMapping("/status/{uid}")
    public ResponseEntity<Mono<String>> getTransactionStatusById(@PathVariable(name = "uid") UUID uid) {
        return ResponseEntity.ok(transactionService.getTransactionStatus(uid));
    }

    @PostMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> receiveTransactionStatus(@RequestBody Transaction transaction) {
        return transactionService.receiveTransactionStatus(transaction);
    }

    @GetMapping
    public ResponseEntity<Flux<TransactionResponseDto>> getAllTransactions(HttpServletRequest request) {
        return ResponseEntity.ok(transactionService.getAllTransactions(request.getRequestURI(), request.getMethod()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<TransactionResponseDto>> getTransaction(@PathVariable(name = "id") UUID uid,
                                                                       HttpServletRequest request) {
        return ResponseEntity.ok(transactionService.getById(uid, request.getRequestURI(), request.getMethod()));
    }
}

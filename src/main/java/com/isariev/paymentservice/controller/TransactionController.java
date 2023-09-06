package com.isariev.paymentservice.controller;

import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import com.isariev.paymentservice.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Mono<TransactionResponseDto>> createTransaction(@RequestBody TransactionRequestDto transactionRequest,
                                                                          HttpServletRequest request) {
        return new ResponseEntity<>(transactionService.createTransaction(transactionRequest, request.getRequestURI(), request.getMethod()), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<Mono<TransactionResponseDto>> createPayout(@RequestBody TransactionRequestDto transactionRequest,
                                                                                HttpServletRequest request) {
        return new ResponseEntity<>(transactionService.createPayout(transactionRequest, request.getRequestURI(), request.getMethod()), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<TransactionResponseDto>> getTransaction(@PathVariable(name = "id") String uid,
                                                                       HttpServletRequest request) {
        return ResponseEntity.ok(transactionService.getById(uid, request.getRequestURI(), request.getMethod()));
    }

    @GetMapping
    public ResponseEntity<Flux<TransactionResponseDto>> getAllTransactions(HttpServletRequest request) {
        return ResponseEntity.ok(transactionService.getAllTransactions(request.getRequestURI(), request.getMethod()));
    }
}

package com.isariev.paymentservice.controller;

import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import com.isariev.paymentservice.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
}

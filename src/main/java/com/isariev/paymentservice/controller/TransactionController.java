package com.isariev.paymentservice.controller;

import com.isariev.paymentservice.dto.payoutResponse.PayoutResponseDto;
import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import com.isariev.paymentservice.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller for managing transactions.
 */
@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Create a new payment transaction.
     *
     * @param transactionRequest The TransactionRequestDto containing the transaction data.
     * @param request            The HttpServletRequest for obtaining request details.
     * @return A ResponseEntity containing the created transaction as a Mono<TransactionResponseDto> and HttpStatus.CREATED.
     */
    @PostMapping
    public ResponseEntity<Mono<TransactionResponseDto>> createTransaction(@RequestBody TransactionRequestDto transactionRequest,
                                                                          HttpServletRequest request) {
        return new ResponseEntity<>(transactionService.createPaymentTransaction(transactionRequest, request.getRequestURI(), request.getMethod()), HttpStatus.CREATED);
    }

    /**
     * Create a new payout transaction.
     *
     * @param transactionRequest The TransactionRequestDto containing the transaction data.
     * @param request            The HttpServletRequest for obtaining request details.
     * @return A ResponseEntity containing the created payout transaction as a Mono<PayoutResponseDto> and HttpStatus.CREATED.
     */
    @PostMapping("/payout")
    public ResponseEntity<Mono<PayoutResponseDto>> createPayout(@RequestBody TransactionRequestDto transactionRequest,
                                                                HttpServletRequest request) {
        return new ResponseEntity<>(transactionService.createPayoutTransaction(transactionRequest, request.getRequestURI(), request.getMethod()), HttpStatus.CREATED);
    }

    /**
     * Get a transaction by its UID.
     *
     * @param uid     The UID of the transaction to retrieve.
     * @param request The HttpServletRequest for obtaining request details.
     * @return A ResponseEntity containing the retrieved transaction as a Mono<TransactionResponseDto>.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<TransactionResponseDto>> getTransaction(@PathVariable(name = "id") String uid,
                                                                       HttpServletRequest request) {
        return ResponseEntity.ok(transactionService.getPaymentTransactionById(uid, request.getRequestURI(), request.getMethod()));
    }

    /**
     * Get a payout transaction by its UID.
     *
     * @param uid     The UID of the payout transaction to retrieve.
     * @param request The HttpServletRequest for obtaining request details.
     * @return A ResponseEntity containing the retrieved payout transaction as a Mono<PayoutResponseDto>.
     */
    @GetMapping("/payout/{id}")
    public ResponseEntity<Mono<PayoutResponseDto>> getPayoutTransaction(@PathVariable(name = "id") String uid,
                                                                        HttpServletRequest request) {
        return ResponseEntity.ok(transactionService.getPayoutTransactionById(uid, request.getRequestURI(), request.getMethod()));
    }

    /**
     * Get all payment transactions.
     *
     * @param request The HttpServletRequest for obtaining request details.
     * @return A ResponseEntity containing a Flux<TransactionResponseDto> with all payment transactions.
     */
    @GetMapping
    public ResponseEntity<Flux<TransactionResponseDto>> getAllTransactions(HttpServletRequest request) {
        return ResponseEntity.ok(transactionService.getAllTransactions(request.getRequestURI(), request.getMethod()));
    }

    /**
     * Get all payout transactions.
     *
     * @param request The HttpServletRequest for obtaining request details.
     * @return A ResponseEntity containing a Flux<PayoutResponseDto> with all payout transactions.
     */
    @GetMapping("/payout")
    public ResponseEntity<Flux<PayoutResponseDto>> getAllPayoutTransactions(HttpServletRequest request) {
        return ResponseEntity.ok(transactionService.getAllPayoutTransactions(request.getRequestURI(), request.getMethod()));
    }

}

package com.isariev.paymentservice.service;

import com.isariev.paymentservice.dto.mapper.CustomerMapper;
import com.isariev.paymentservice.dto.mapper.TransactionMapper;
import com.isariev.paymentservice.dto.payoutResponse.PayoutFormResponseDto;
import com.isariev.paymentservice.dto.payoutResponse.PayoutResponseDto;
import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.FormResponseDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import com.isariev.paymentservice.exception.ConvertIdException;
import com.isariev.paymentservice.exception.EntityNotFoundException;
import com.isariev.paymentservice.model.Customer;
import com.isariev.paymentservice.model.Transaction;
import com.isariev.paymentservice.repository.CustomerRepository;
import com.isariev.paymentservice.repository.TransactionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service for managing transactions.
 */
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionMapper transactionMapper;
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Constructs a new TransactionServiceImpl with the provided dependencies.
     *
     * @param transactionMapper     The mapper for transactions.
     * @param customerMapper        The mapper for customers.
     * @param customerRepository    The repository for customers.
     * @param transactionRepository The repository for transactions.
     */
    public TransactionServiceImpl(TransactionMapper transactionMapper, CustomerMapper customerMapper, CustomerRepository customerRepository, TransactionRepository transactionRepository) {
        this.transactionMapper = transactionMapper;
        this.customerMapper = customerMapper;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Create a new payment transaction.
     *
     * @param transactionRequest The TransactionRequestDto containing the transaction data.
     * @param requestURI         The request URI for obtaining request details.
     * @param method             The request method for obtaining request details.
     * @return A Mono<TransactionResponseDto> representing the created payment transaction.
     */
    @Override
    public Mono<TransactionResponseDto> createPaymentTransaction(TransactionRequestDto transactionRequest, String requestURI, String method) {
        return createTransaction(transactionRequest, "payment")
                .map(response -> transactionMapper.toResponseDto(response, new FormResponseDto(requestURI, method)));
    }

    /**
     * Create a new payout transaction.
     *
     * @param transactionRequest The TransactionRequestDto containing the transaction data.
     * @param requestURI         The request URI for obtaining request details.
     * @param method             The request method for obtaining request details.
     * @return A Mono<PayoutResponseDto> representing the created payout transaction.
     */
    @Override
    public Mono<PayoutResponseDto> createPayoutTransaction(TransactionRequestDto transactionRequest, String requestURI, String method) {
        return createTransaction(transactionRequest, "payout")
                .map(response -> transactionMapper.toResponseDto(response, new PayoutFormResponseDto(requestURI, method)));
    }

    /**
     * Get a payment transaction by its ID.
     *
     * @param id         The ID of the payment transaction to retrieve.
     * @param requestURI The request URI for obtaining request details.
     * @param method     The request method for obtaining request details.
     * @return A Mono<TransactionResponseDto> representing the retrieved payment transaction.
     */
    @Override
    public Mono<TransactionResponseDto> getPaymentTransactionById(String id, String requestURI, String method) {
        return getTransactionById(id)
                .map(transaction -> transactionMapper.toResponseDto(transaction, new FormResponseDto(requestURI, method)));
    }

    /**
     * Get a payout transaction by its ID.
     *
     * @param id         The ID of the payout transaction to retrieve.
     * @param requestURI The request URI for obtaining request details.
     * @param method     The request method for obtaining request details.
     * @return A Mono<PayoutResponseDto> representing the retrieved payout transaction.
     */
    @Override
    public Mono<PayoutResponseDto> getPayoutTransactionById(String id, String requestURI, String method) {
        return getTransactionById(id)
                .map(transaction -> transactionMapper.toResponseDto(transaction, new PayoutFormResponseDto(requestURI, method)));
    }

    /**
     * Get all payment transactions.
     *
     * @param requestURI The request URI for obtaining request details.
     * @param method     The request method for obtaining request details.
     * @return A Flux<TransactionResponseDto> representing all payment transactions.
     */
    @Override
    public Flux<TransactionResponseDto> getAllTransactions(String requestURI, String method) {
        Flux<Transaction> transactionFlux = transactionRepository.findAllByType("payment");

        return transformTransactions(transactionFlux)
                .map(transaction -> transactionMapper.toResponseDto(transaction, new FormResponseDto(requestURI, method)));
    }

    /**
     * Get all payout transactions.
     *
     * @param requestURI The request URI for obtaining request details.
     * @param method     The request method for obtaining request details.
     * @return A Flux<PayoutResponseDto> representing all payout transactions.
     */
    @Override
    public Flux<PayoutResponseDto> getAllPayoutTransactions(String requestURI, String method) {
        Flux<Transaction> transactionFlux = transactionRepository.findAllByType("payout");

        return transformTransactions(transactionFlux)
                .map(transaction -> transactionMapper.toResponseDto(transaction, new PayoutFormResponseDto(requestURI, method)));
    }

    /**
     * Create a new transaction.
     *
     * @param transactionRequest The TransactionRequestDto containing the transaction data.
     * @param type               The type of the transaction (e.g., "payment" or "payout").
     * @return A Mono<Transaction> representing the created transaction.
     */
    private Mono<Transaction> createTransaction(TransactionRequestDto transactionRequest, String type) {
        return customerRepository.findByEmail(transactionRequest.customer().email())
                .switchIfEmpty(Mono.defer(() -> {
                    Customer customer = customerMapper.toEntity(transactionRequest.customer());
                    return customerRepository.save(customer);
                }))
                .flatMap(savedCustomer -> {
                    Transaction transaction = transactionMapper.toEntity(transactionRequest);
                    transaction.setType(type);
                    transaction.setCustomerId(savedCustomer.getUid());
                    transaction.setCustomer(savedCustomer);
                    return transactionRepository.save(transaction);
                })
                .onErrorMap(ex -> new RuntimeException("Error creating transaction", ex));
    }

    /**
     * Transform a Flux of transactions by adding customer information.
     *
     * @param transactionFlux The Flux of transactions to transform.
     * @return A Flux<Transaction> with customer information added to each transaction.
     */
    @NotNull
    private Flux<Transaction> transformTransactions(Flux<Transaction> transactionFlux) {
        return transactionFlux.flatMap(transaction -> {
            Mono<Customer> customerMono = customerRepository.findById(transaction.getCustomerId());
            return customerMono.map(customer -> {
                transaction.setCustomer(customer);
                return transaction;
            });
        });
    }

    /**
     * Get a transaction by its ID.
     *
     * @param id The ID of the transaction to retrieve.
     * @return A Mono<Transaction> representing the retrieved transaction.
     */
    private Mono<Transaction> getTransactionById(String id) {
        UUID uid = getUuid(id);

        Mono<Transaction> transactionMono = transactionRepository.findById(uid)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Transaction not found with ID: " + id)));
        Mono<Customer> customerMono = transactionMono.flatMap(t -> customerRepository.findById(t.getCustomerId()));

        return Mono.zip(transactionMono, customerMono).map(tuple -> {
            Transaction transaction = tuple.getT1();
            transaction.setCustomer(tuple.getT2());
            return transaction;
        });
    }

    /**
     * Get a UUID from a string representation.
     *
     * @param id The string representation of the UUID.
     * @return The UUID object.
     */
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

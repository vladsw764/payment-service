package com.isariev.paymentservice.service;

import com.isariev.paymentservice.dto.mapper.CustomerMapper;
import com.isariev.paymentservice.dto.mapper.TransactionMapper;
import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.FormResponseDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import com.isariev.paymentservice.exception.ConvertIdException;
import com.isariev.paymentservice.exception.EntityNotFoundException;
import com.isariev.paymentservice.model.Customer;
import com.isariev.paymentservice.model.Transaction;
import com.isariev.paymentservice.repository.CustomerRepository;
import com.isariev.paymentservice.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionMapper transactionMapper;
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final WebhookService webhookService;

    public TransactionServiceImpl(TransactionMapper transactionMapper, CustomerMapper customerMapper, CustomerRepository customerRepository, TransactionRepository transactionRepository, WebhookService webhookService) {
        this.transactionMapper = transactionMapper;
        this.customerMapper = customerMapper;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.webhookService = webhookService;
    }

    @Override
    public Mono<TransactionResponseDto> createTransaction(TransactionRequestDto transactionRequest, String requestURI, String method) {
        return createPayoutOrTransaction(transactionRequest, requestURI, method, "payment");
    }

    @Override
    public Mono<TransactionResponseDto> createPayout(TransactionRequestDto transactionRequest, String requestURI, String method) {
        return createPayoutOrTransaction(transactionRequest, requestURI, method, "payout");
    }

    @Override
    public Mono<String> getTransactionStatus(String id) {
        return transactionRepository.getStatusById(getUuid(id));
    }

    @Override
    public Mono<TransactionResponseDto> getById(String id, String requestURI, String method) {
        UUID uid = getUuid(id);

        Mono<Transaction> transactionMono = transactionRepository.findById(uid)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Transaction not found with ID: " + id)));
        Mono<Customer> customerMono = transactionMono.flatMap(t -> customerRepository.findById(t.getCustomerId()));

        return Mono.zip(transactionMono, customerMono).map(tuple -> {
            Transaction transaction = tuple.getT1();
            transaction.setCustomer(tuple.getT2());
            return transactionMapper.toResponseDto(transaction, new FormResponseDto(requestURI, method));
        });
    }

    @Override
    public Flux<TransactionResponseDto> getAllTransactions(String requestURI, String method) {
        Flux<Transaction> transactionFlux = transactionRepository.findAll();

        return transactionFlux.flatMap(transaction -> {
            Mono<Customer> customerMono = customerRepository.findById(transaction.getCustomerId());
            return customerMono.map(customer -> {
                transaction.setCustomer(customer);
                return transactionMapper.toResponseDto(transaction, new FormResponseDto(requestURI, method));
            });
        });
    }

    @Override
    public Mono<Void> receiveTransactionStatus(Transaction transaction) {
        log.info(transaction.getStatus());
        return Mono.empty();
    }

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

    private Mono<TransactionResponseDto> createPayoutOrTransaction(TransactionRequestDto transactionRequest, String requestURI, String method, String transactionType) {
        FormResponseDto formResponse = new FormResponseDto(requestURI, method);

        return customerRepository.findByEmail(transactionRequest.customer().email())
                .switchIfEmpty(Mono.defer(() -> {
                    Customer customer = customerMapper.toEntity(transactionRequest.customer());
                    return customerRepository.save(customer);
                }))
                .flatMap(savedCustomer -> {
                    Transaction transaction = transactionMapper.toEntity(transactionRequest);
                    transaction.setType(transactionType);
                    transaction.setCustomerId(savedCustomer.getUid());
                    transaction.setCustomer(savedCustomer);
                    return transactionRepository.save(transaction);
                })
                .map(response ->
                        transactionMapper.toResponseDto(response, formResponse)
                )
                .onErrorMap(ex -> new RuntimeException("Error creating transaction", ex));
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

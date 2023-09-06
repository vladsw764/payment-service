package com.isariev.paymentservice.service;

import com.isariev.paymentservice.dto.mapper.CustomerMapper;
import com.isariev.paymentservice.dto.mapper.TransactionMapper;
import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.FormResponseDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
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
    public Mono<TransactionResponseDto> createTransaction(TransactionRequestDto transactionRequest) {
        Mono<Customer> savedCustomer = customerRepository.save(
                customerMapper.toEntity(transactionRequest.customer())
        );

        Mono<Transaction> transactionMono = savedCustomer.flatMap(customer -> {
            Transaction transaction = transactionMapper.toEntity(transactionRequest);
            transaction.setCustomerId(customer.getUid());
            transaction.setCustomer(customer);
            return transactionRepository.save(transaction);
        });

        return transactionMono
                .flatMap(transaction -> Mono.just(transactionMapper.toResponseDto(transaction)));
    }

    @Override
    public Mono<String> getTransactionStatus(UUID uid) {
        return transactionRepository.getStatusById(uid);
    }

    @Override
    public Mono<TransactionResponseDto> getById(UUID id, String requestURI, String method) {
        Mono<Transaction> transactionMono = transactionRepository.findById(id);
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
}

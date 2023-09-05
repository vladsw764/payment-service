package com.isariev.paymentservice.service;

import com.isariev.paymentservice.dto.mapper.CustomerMapper;
import com.isariev.paymentservice.dto.mapper.TransactionMapper;
import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import com.isariev.paymentservice.model.Customer;
import com.isariev.paymentservice.model.Transaction;
import com.isariev.paymentservice.repository.CustomerRepository;
import com.isariev.paymentservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionMapper transactionMapper;
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionMapper transactionMapper, CustomerMapper customerMapper, CustomerRepository customerRepository, TransactionRepository transactionRepository) {
        this.transactionMapper = transactionMapper;
        this.customerMapper = customerMapper;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
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
}

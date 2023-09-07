package com.isariev.paymentservice.controller;

import com.isariev.paymentservice.dto.request.CustomerRequestDto;
import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(locations = "classpath:application-test.yml")
public class TransactionStatusControllerIntegrationTest {

    @Autowired
    private WebTestClient testClient;
    private TransactionRequestDto requestDto;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        requestDto = new TransactionRequestDto(
                7800,
                "USD",
                "http://localhost:" + port + "/v1/status",
                "merchant.com/main",
                new CustomerRequestDto(
                        "Vlad",
                        "Isariev",
                        "vladsw76@gmail.com",
                        "UA",
                        "88005553535"
                ),
                true
        );
    }

    @Test
    void getTransactionStatusById() {
        testClient.get()
                .uri("/v1/transactions/{uid}", createTransactionAndGetId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> assertThat(responseBody).isNotNull());
    }


    private String createTransactionAndGetId() {
        TransactionResponseDto responseDto = testClient.post()
                .uri("/v1/transactions")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.uid()).isNotNull();

        return responseDto.uid();
    }
}

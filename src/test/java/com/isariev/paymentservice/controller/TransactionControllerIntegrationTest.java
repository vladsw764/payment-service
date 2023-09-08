package com.isariev.paymentservice.controller;

import com.isariev.paymentservice.TestPaymentServiceApplication;
import com.isariev.paymentservice.dto.payoutResponse.PayoutResponseDto;
import com.isariev.paymentservice.dto.request.CustomerRequestDto;
import com.isariev.paymentservice.dto.request.TransactionRequestDto;
import com.isariev.paymentservice.dto.response.TransactionResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionControllerIntegrationTest {
    private static final PostgreSQLContainer<?> postgresContainer = TestPaymentServiceApplication.postgresContainer();

    @Autowired
    private WebTestClient testClient;

    private TransactionRequestDto requestDto;

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://localhost:7777/integration-tests-db");
        registry.add("spring.r2dbc.username", () -> "postgres");
        registry.add("spring.r2dbc.password", () -> "postgres");
        registry.add("spring.flyway.url", () -> "jdbc:postgresql://localhost:7777/integration-tests-db");
        registry.add("spring.flyway.username", () -> "postgres");
        registry.add("spring.flyway.password", () -> "postgres");
    }

    @BeforeAll
    static void startContainer() {
        if (!postgresContainer.isRunning()) {
            postgresContainer.start();
        }
    }

    @AfterAll
    static void stopContainer() {
        postgresContainer.stop();
    }

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
    @Order(1)
    void createTransaction() {
        testClient.post()
                .uri("/v1/transactions")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionResponseDto.class)
                .value(responseDto -> {
                    assertThat(responseDto).isNotNull();
                    assertThat(responseDto.uid()).isNotNull();
                    assertThat(responseDto.amount()).isEqualTo(7800);
                    assertThat(responseDto.currency()).isEqualTo("USD");
                });
    }

    @Test
    @Order(2)
    void createPayout() {
        testClient.post()
                .uri("/v1/transactions/payout")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionResponseDto.class)
                .value(responseDto -> {
                    assertThat(responseDto).isNotNull();
                    assertThat(responseDto.uid()).isNotNull();
                    assertThat(responseDto.amount()).isEqualTo(7800);
                    assertThat(responseDto.currency()).isEqualTo("USD");
                });
    }

    @Test
    @Order(3)
    void getTransaction() {
        String transactionId = createTransactionAndGetId();
        testClient.get()
                .uri("/v1/transactions/{id}", transactionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDto.class)
                .value(responseDto -> {
                    assertThat(responseDto).isNotNull();
                    assertThat(responseDto.uid()).isEqualTo(transactionId);
                });
    }

    @Test
    @Order(4)
    void getPayoutTransaction() {
        String payoutTransactionId = createPayoutTransactionAndGetId();
        testClient.get()
                .uri("/v1/transactions/payout/{id}", payoutTransactionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PayoutResponseDto.class)
                .value(responseDto -> {
                    assertThat(responseDto).isNotNull();
                    assertThat(responseDto.uid()).isEqualTo(payoutTransactionId);
                });
    }

    @Test
    @Order(5)
    void getAllTransactions() {
        testClient.get()
                .uri("/v1/transactions")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionResponseDto.class)
                .value(responseDtoList -> {
                    assertThat(responseDtoList).isNotNull();
                    assertThat(responseDtoList).isNotEmpty();
                });
    }

    @Test
    @Order(6)
    void getAllPayoutTransactions() {
        testClient.get()
                .uri("/v1/transactions/payout")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PayoutResponseDto.class)
                .value(responseDtoList -> {
                    assertThat(responseDtoList).isNotNull();
                    assertThat(responseDtoList).isNotEmpty();
                });
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

    private String createPayoutTransactionAndGetId() {
        PayoutResponseDto responseDto = testClient.post()
                .uri("/v1/transactions/payout")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PayoutResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.uid()).isNotNull();

        return responseDto.uid();
    }

}
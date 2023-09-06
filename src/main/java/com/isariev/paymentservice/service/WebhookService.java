package com.isariev.paymentservice.service;

import com.isariev.paymentservice.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WebhookService {

    private final WebClient webClient;

    public WebhookService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<Void> sendTransactionWebhook(Transaction transaction) {
        return webClient.post()
                .uri(transaction.getNotificationUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(transaction))
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.info("Webhook sent successfully");
                    } else {
                        log.error("Failed to send webhook");
                    }
                })
                .then();
    }
}

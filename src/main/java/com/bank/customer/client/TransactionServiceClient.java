package com.bank.customer.client;

import com.bank.customer.model.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionServiceClient {

  private final WebClient webClient;

  @Value("${external.services.transaction.url:http://localhost:8084}")
  private String transactionServiceUrl;

  public Flux<TransactionResponse> getRecentTransactions(String customerId) {
    log.info("Getting recent transactions for customer: {}", customerId);

    Map<String, Object> params = new HashMap<>();
    params.put("customerId", customerId);

    return webClient.get()
      .uri(transactionServiceUrl + "/transactions/customer/{customerId}", customerId)
      .retrieve()
      .bodyToFlux(new ParameterizedTypeReference<TransactionResponse>() {})
      .doOnError(ex -> {
        log.error("Error fetching transactions for customer {}: {}", customerId, ex.getMessage());
      })
      .onErrorResume(ex -> Flux.empty());
  }

  public Flux<TransactionResponse> getTransactionsByDateRange(String startDate, String endDate) {
    log.info("Getting transactions from {} to {}", startDate, endDate);

    return webClient.get()
      .uri(transactionServiceUrl + "/transactions?startDate={startDate}&endDate={endDate}",
        startDate, endDate)
      .retrieve()
      .bodyToFlux(new ParameterizedTypeReference<TransactionResponse>() {})
      .doOnError(ex -> {
        log.error("Error fetching transactions by date range: {}", ex.getMessage());
      })
      .onErrorResume(ex -> Flux.empty());
  }

  // O si necesitas también getAllTransactions
  public Flux<TransactionResponse> getAllTransactions() {
    log.info("Getting all transactions");

    return webClient.get()
      .uri(transactionServiceUrl + "/transactions")
      .retrieve()
      .bodyToFlux(new ParameterizedTypeReference<TransactionResponse>() {})
      .doOnError(ex -> {
        log.error("Error fetching all transactions: {}", ex.getMessage());
      })
      .onErrorResume(ex -> Flux.empty());
  }
}
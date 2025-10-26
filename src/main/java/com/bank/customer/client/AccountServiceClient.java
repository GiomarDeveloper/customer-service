package com.bank.customer.client;

import com.bank.customer.model.AccountSummary;
import com.bank.customer.model.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountServiceClient {

  private final WebClient webClient;

  @Value("${external.services.account.url:http://localhost:8082}")
  private String accountServiceUrl;

  public Flux<AccountSummary> getCustomerAccountsWithDailyBalances(String customerId) {
    log.info("Getting accounts with daily balances for customer: {}", customerId);

    return webClient.get()
      .uri(accountServiceUrl + "/accounts/customer/{customerId}/daily-balances", customerId)
      .retrieve()
      .bodyToFlux(new ParameterizedTypeReference<AccountSummary>() {})
      .doOnError(ex -> {
        log.error("Error fetching accounts for customer {}: {}", customerId, ex.getMessage());
      })
      .onErrorResume(ex -> Flux.empty());
  }

  public Flux<AccountResponse> getCustomerAccounts(String customerId) {
    log.info("Getting accounts for customer: {}", customerId);

    return webClient.get()
      .uri(accountServiceUrl + "/accounts/customer/{customerId}", customerId)
      .retrieve()
      .bodyToFlux(new ParameterizedTypeReference<AccountResponse>() {})
      .doOnError(ex -> {
        log.error("Error fetching accounts for customer {}: {}", customerId, ex.getMessage());
      })
      .onErrorResume(ex -> Flux.empty());
  }

  public Flux<AccountResponse> getAllAccounts() {
    log.info("Getting all accounts");

    return webClient.get()
      .uri(accountServiceUrl + "/accounts")
      .retrieve()
      .bodyToFlux(new ParameterizedTypeReference<AccountResponse>() {})
      .doOnError(ex -> {
        log.error("Error fetching all accounts: {}", ex.getMessage());
      })
      .onErrorResume(ex -> Flux.empty());
  }
}
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

/**
 * Cliente para comunicación con el servicio de Cuentas.
 * Maneja todas las llamadas API relacionadas con cuentas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountServiceClient {

  private final WebClient webClient;

  @Value("${external.services.account.url:http://localhost:8082}")
  private String accountServiceUrl;

  /**
   * Obtiene las cuentas del cliente con balances diarios.
   *
   * @param customerId el ID del cliente
   * @return Flux de AccountSummary con balances diarios
   */
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

  /**
   * Obtiene todas las cuentas de un cliente específico.
   *
   * @param customerId el ID del cliente
   * @return Flux de AccountResponse
   */
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

  /**
   * Obtiene todas las cuentas del sistema.
   *
   * @return Flux de AccountResponse
   */
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
package com.bank.customer.client;

import com.bank.customer.model.CreditSummary;
import com.bank.customer.model.response.CreditResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * Cliente para comunicación con el servicio de Créditos.
 * Maneja todas las llamadas API relacionadas con créditos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreditServiceClient {

  private final WebClient webClient;

  @Value("${external.services.credit.url:http://localhost:8083}")
  private String creditServiceUrl;

  /**
   * Obtiene los créditos del cliente con balances diarios.
   *
   * @param customerId el ID del cliente
   * @return Flux de CreditSummary con balances diarios
   */
  public Flux<CreditSummary> getCustomerCreditsWithDailyBalances(String customerId) {
    log.info("Getting credits with daily balances for customer: {}", customerId);

    return webClient.get()
      .uri(creditServiceUrl + "/credits/customer/{customerId}/daily-balances", customerId)
      .retrieve()
      .bodyToFlux(new ParameterizedTypeReference<CreditSummary>() {})
      .doOnError(ex -> {
        log.error("Error fetching credits for customer {}: {}", customerId, ex.getMessage());
      })
      .onErrorResume(ex -> Flux.empty());
  }

  /**
   * Obtiene todos los créditos de un cliente específico.
   *
   * @param customerId el ID del cliente
   * @return Flux de CreditResponse
   */
  public Flux<CreditResponse> getCustomerCredits(String customerId) {
    log.info("Getting credits for customer: {}", customerId);

    return webClient.get()
      .uri(creditServiceUrl + "/credits/customer/{customerId}", customerId)
      .retrieve()
      .bodyToFlux(new ParameterizedTypeReference<CreditResponse>() {})
      .doOnError(ex -> {
        log.error("Error fetching credits for customer {}: {}", customerId, ex.getMessage());
      })
      .onErrorResume(ex -> Flux.empty());
  }

  /**
   * Obtiene todos los créditos del sistema.
   *
   * @return Flux de CreditResponse
   */
  public Flux<CreditResponse> getAllCredits() {
    log.info("Getting all credits");

    return webClient.get()
      .uri(creditServiceUrl + "/credits")
      .retrieve()
      .bodyToFlux(new ParameterizedTypeReference<CreditResponse>() {})
      .doOnError(ex -> {
        log.error("Error fetching all credits: {}", ex.getMessage());
      })
      .onErrorResume(ex -> Flux.empty());
  }
}
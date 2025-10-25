package com.bank.customer.controller;

import com.bank.customer.api.CustomersApi;
import com.bank.customer.model.CustomerMonthlySummary;
import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerResponse;
import com.bank.customer.service.CustomerService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

  private final CustomerService service;

  @Override
  public Mono<ResponseEntity<CustomerResponse>> create(
      @Valid @RequestBody Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
    return customerRequest
      .flatMap(service::create)
      .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
  }

  @Override
  public Mono<ResponseEntity<Void>> delete(String id, ServerWebExchange exchange) {
    return service.delete(id)
      .then(Mono.just(ResponseEntity.noContent().build()));
  }

  @Override
  public Mono<ResponseEntity<Flux<CustomerResponse>>> getAll(ServerWebExchange exchange) {
    return Mono.just(ResponseEntity.ok(service.findAll()));
  }

  @Override
  public Mono<ResponseEntity<CustomerResponse>> getByDocument(String documentNumber,
                                                              ServerWebExchange exchange) {
    return service.findByDocumentNumber(documentNumber)
      .map(ResponseEntity::ok)
      .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @Override
  public Mono<ResponseEntity<CustomerResponse>> getById(String id, ServerWebExchange exchange) {
    return service.findById(id)
      .map(ResponseEntity::ok)
      .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @Override
  public Mono<ResponseEntity<CustomerResponse>> update(String id, @Valid @RequestBody
      Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
    return customerRequest
      .flatMap(req -> service.update(id, req))
      .map(ResponseEntity::ok)
      .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @Override
  public Mono<ResponseEntity<CustomerMonthlySummary>> getCustomerMonthlySummary(String customerId,
                                                                                ServerWebExchange exchange) {
    return service.generateMonthlySummary(customerId)
      .map(ResponseEntity::ok)
      .onErrorResume(ex -> {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
      });
  }
}
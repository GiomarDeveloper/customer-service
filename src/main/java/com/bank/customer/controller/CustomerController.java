package com.bank.customer.controller;

import com.bank.customer.api.CustomersApi;
import com.bank.customer.mapper.CustomerMapper;
import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerResponse;
import com.bank.customer.service.CustomerService;
import com.bank.customer.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService service;
    private final CustomerMapper mapper;
    private final ValidationHelper validationHelper;

    @Override
    public Mono<ResponseEntity<CustomerResponse>> create(@Valid @RequestBody Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
        return customerRequest
                .doOnNext(validationHelper::validate) // Validación con helper
                .map(mapper::toEntity)
                .flatMap(service::create)
                .map(mapper::toResponse)
                .map(cr -> ResponseEntity.status(HttpStatus.CREATED).body(cr))
                .onErrorResume(WebExchangeBindException.class, ex -> {
                    return Mono.error(ex);
                })
                .doOnSuccess(response -> log.info("Customer created successfully"))
                .doOnError(error -> log.error("Error creating customer: {}", error.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<Void>> delete(String id, ServerWebExchange exchange) {
        return service.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Override
    public Mono<ResponseEntity<Flux<CustomerResponse>>> getAll(ServerWebExchange exchange) {
        Flux<CustomerResponse> customers = service.findAll()
                .map(mapper::toResponse);

        return Mono.just(ResponseEntity.ok(customers));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> getByDocument(String documentNumber, ServerWebExchange exchange) {
        return service.findByDocumentNumber(documentNumber)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Retrieved customer with document number: {}", documentNumber))
                .doOnError(error -> log.error("Error retrieving customer with document number {}: {}", documentNumber, error.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> getById(String id, ServerWebExchange exchange) {
        return service.findById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Retrieved customer with id: {}", id))
                .doOnError(error -> log.error("Error retrieving customer with id {}: {}", id, error.getMessage()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> update(String id,  @Valid @RequestBody Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
        return customerRequest
                .doOnNext(validationHelper::validate) // Validación con helper
                .map(mapper::toEntity)
                .flatMap(customer -> service.update(id, customer))
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(WebExchangeBindException.class, ex -> {
                    return Mono.error(ex);
                })
                .doOnSuccess(response -> log.info("Customer updated successfully with id: {}", id))
                .doOnError(error -> log.error("Error updating customer with id {}: {}", id, error.getMessage()));
    }
}
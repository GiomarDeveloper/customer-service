package com.bank.customer.service;

import com.bank.customer.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<Customer> findAll();
    Mono<Customer> findById(String id);
    Mono<Customer> findByDocumentNumber(String documentNumber);
    Mono<Customer> create(Customer customer);
    Mono<Customer> update(String id, Customer customer);
    Mono<Void> delete(String id);
}
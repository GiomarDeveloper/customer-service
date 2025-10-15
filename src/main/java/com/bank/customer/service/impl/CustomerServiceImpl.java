package com.bank.customer.service.impl;

import com.bank.customer.exception.ResourceNotFoundException;
import com.bank.customer.model.Customer;
import com.bank.customer.repository.CustomerRepository;
import com.bank.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo;

    @Override
    public Flux<Customer> findAll() {
        return repo.findAll();
    }

    @Override
    public Mono<Customer> findById(String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found with id: " + id)));
    }

    @Override
    public Mono<Customer> findByDocumentNumber(String documentNumber) {
        return repo.findByDocumentNumber(documentNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Customer not found with document number: " + documentNumber)));
    }

    @Override
    public Mono<Customer> create(Customer customer) {
        return repo.findByDocumentNumber(customer.getDocumentNumber())
                .flatMap(existing -> Mono.<Customer>error(
                        new RuntimeException("Customer with document number already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    customer.setCreatedAt(Instant.now());
                    return repo.save(customer);
                }));
    }

    @Override
    public Mono<Customer> update(String id, Customer customer) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found with ID " + id)))
                .flatMap(existing -> {
                    existing.setFirstName(customer.getFirstName());
                    existing.setLastName(customer.getLastName());
                    existing.setEmail(customer.getEmail());
                    existing.setPhone(customer.getPhone());
                    existing.setCustomerType(customer.getCustomerType());
                    existing.setDocumentNumber(customer.getDocumentNumber());
                    return repo.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found with ID " + id)))
                .flatMap(repo::delete);
    }
}

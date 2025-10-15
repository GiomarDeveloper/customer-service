package com.bank.customer.service.impl;

import com.bank.customer.exception.ResourceNotFoundException;
import com.bank.customer.mapper.CustomerMapper;
import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerResponse;
import com.bank.customer.repository.CustomerRepository;
import com.bank.customer.service.CustomerService;
import com.bank.customer.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper mapper;
    private final ValidationHelper validationHelper;
    private final CustomerRepository repo;

    @Override
    public Flux<CustomerResponse> findAll() {
        return repo.findAll()
                .map(mapper::toResponse)
                .doOnComplete(() -> log.info("Retrieved all customers successfully"))
                .doOnError(error -> log.error("Error retrieving customers: {}", error.getMessage(), error));
    }

    @Override
    public Mono<CustomerResponse> findById(String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found with id: " + id)))
                .map(mapper::toResponse)
                .doOnSuccess(response -> log.info("Retrieved customer successfully with id: {}", id))
                .doOnError(error -> log.error("Error retrieving customer with id {}: {}", id, error.getMessage(), error));
    }

    @Override
    public Mono<CustomerResponse> findByDocumentNumber(String documentNumber) {
        return repo.findByDocumentNumber(documentNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Customer not found with document number: " + documentNumber)))
                .map(mapper::toResponse)
                .doOnSuccess(response -> log.info("Retrieved customer successfully with document: {}", documentNumber))
                .doOnError(error -> log.error("Error retrieving customer with document {}: {}", documentNumber, error.getMessage(), error));
    }

    @Override
    public Mono<CustomerResponse> create(CustomerRequest request) {
        return validationHelper.validateAsync(request)
                .map(mapper::toEntity)
                .flatMap(customer -> repo.findByDocumentNumber(customer.getDocumentNumber())
                        .flatMap(existing -> Mono.<Customer>error(
                                new RuntimeException("Customer with document number already exists")))
                        .switchIfEmpty(Mono.defer(() -> {
                            customer.setCreatedAt(Instant.now());
                            return repo.save(customer);
                        }))
                )
                .map(mapper::toResponse)
                .doOnSuccess(response -> log.info("Customer created successfully with id: {}", response.getId()))
                .doOnError(error -> log.error("Error creating customer: {}", error.getMessage(), error));
    }


    @Override
    public Mono<CustomerResponse> update(String id, CustomerRequest request) {
        return validationHelper.validateAsync(request)
                .map(mapper::toEntity)
                .flatMap(customer -> repo.findById(id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found with ID " + id)))
                        .flatMap(existing -> {
                            existing.setFirstName(customer.getFirstName());
                            existing.setLastName(customer.getLastName());
                            existing.setEmail(customer.getEmail());
                            existing.setPhone(customer.getPhone());
                            existing.setCustomerType(customer.getCustomerType());
                            existing.setDocumentNumber(customer.getDocumentNumber());
                            return repo.save(existing);
                        }))
                .map(mapper::toResponse)
                .doOnSuccess(response -> log.info("Customer updated successfully with id: {}", id))
                .doOnError(error -> log.error("Error updating customer with id {}: {}", id, error.getMessage(), error));
    }

    @Override
    public Mono<Void> delete(String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found with ID " + id)))
                .flatMap(repo::delete)
                .doOnSuccess(unused -> log.info("Customer deleted successfully with id: {}", id))
                .doOnError(error -> log.error("Error deleting customer with id {}: {}", id, error.getMessage(), error));
    }
}

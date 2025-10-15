package com.bank.customer.controller;

import com.bank.customer.dto.CustomerRequest;
import com.bank.customer.dto.CustomerResponse;
import com.bank.customer.mapper.CustomerMapper;
import com.bank.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;
    private final CustomerMapper mapper;

    @GetMapping
    public Flux<CustomerResponse> getAll() {
        return service.findAll()
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public Mono<CustomerResponse> getById(@PathVariable String id) {
        return service.findById(id)
                .map(mapper::toResponse);
    }

    @GetMapping("/by-document/{documentNumber}")
    public Mono<CustomerResponse> getByDocument(@PathVariable String documentNumber) {
        return service.findByDocumentNumber(documentNumber).map((mapper::toResponse));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return service.create(mapper.toEntity(request))
                .map(mapper::toResponse);
    }

    @PutMapping("/{id}")
    public Mono<CustomerResponse> update(@PathVariable String id,
                                         @Valid @RequestBody CustomerRequest request) {
        return service.update(id, mapper.toEntity(request))
                .map(mapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }
}
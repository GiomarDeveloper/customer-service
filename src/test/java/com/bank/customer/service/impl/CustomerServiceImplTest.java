package com.bank.customer.service.impl;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.customer.exception.ResourceNotFoundException;
import com.bank.customer.mapper.CustomerMapper;
import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerResponse;
import com.bank.customer.model.CustomerTypeEnum;
import com.bank.customer.repository.CustomerRepository;
import com.bank.customer.util.ValidationHelper;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CustomerServiceImplTest {

  @Mock
  private CustomerRepository repository;

  @Mock
  private CustomerMapper mapper;

  @Mock
  private ValidationHelper validationHelper;

  @InjectMocks
  private CustomerServiceImpl service;

  private Customer customer;
  private CustomerRequest request;
  private CustomerResponse response;

  private CustomerRequest createRequest() {
    CustomerRequest r = new CustomerRequest();
    r.setDocumentNumber("123");
    r.setFirstName("Giomar");
    r.setCustomerType(CustomerTypeEnum.PERSONAL);
    return r;
  }

  private CustomerResponse createResponse() {
    CustomerResponse res = new CustomerResponse();
    res.setId("1");
    res.setFirstName("Giomar");
    return res;
  }

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    customer = Customer.builder()
      .id("1")
      .documentNumber("123")
      .firstName("Giomar")
      .customerType("PERSONAL")
      .createdAt(Instant.now())
      .build();

    request = createRequest();
    response = createResponse();

    when(mapper.toResponse(any())).thenReturn(response);
    when(mapper.toEntity(any())).thenReturn(customer);
    when(validationHelper.validateAsync(any())).thenReturn(Mono.just(request));
  }

  @Test
  void testFindAllSuccess() {
    when(repository.findAll()).thenReturn(Flux.just(customer));

    StepVerifier.create(service.findAll())
      .expectNext(response)
        .verifyComplete();

    verify(repository).findAll();
  }

  @Test
  void testFindByIdSuccess() {
    when(repository.findById("1")).thenReturn(Mono.just(customer));

    StepVerifier.create(service.findById("1"))
      .expectNext(response)
        .verifyComplete();
  }

  @Test
  void testFindByIdNotFound() {
    when(repository.findById("99")).thenReturn(Mono.empty());

    StepVerifier.create(service.findById("99"))
      .expectError(ResourceNotFoundException.class)
        .verify();
  }

  @Test
  void testCreateSuccess() {
    when(repository.findByDocumentNumber(any())).thenReturn(Mono.empty());
    when(repository.save(any())).thenReturn(Mono.just(customer));

    StepVerifier.create(service.create(request))
      .expectNext(response)
        .verifyComplete();
  }

  @Test
  void testCreateDuplicate() {
    when(repository.findByDocumentNumber(any())).thenReturn(Mono.just(customer));

    StepVerifier.create(service.create(request))
      .expectError(RuntimeException.class)
        .verify();
  }

  @Test
  void testUpdateSuccess() {
    when(repository.findById("1")).thenReturn(Mono.just(customer));
    when(repository.save(any())).thenReturn(Mono.just(customer));

    StepVerifier.create(service.update("1", request))
      .expectNext(response)
        .verifyComplete();
  }

  @Test
  void testUpdateNotFound() {
    when(repository.findById("1")).thenReturn(Mono.empty());

    StepVerifier.create(service.update("1", request))
      .expectError(ResourceNotFoundException.class)
        .verify();
  }

  @Test
  void testDeleteSuccess() {
    when(repository.findById("1")).thenReturn(Mono.just(customer));
    when(repository.delete(customer)).thenReturn(Mono.empty());

    StepVerifier.create(service.delete("1"))
        .verifyComplete();
  }

  @Test
  void testDeleteNotFound() {
    when(repository.findById("1")).thenReturn(Mono.empty());

    StepVerifier.create(service.delete("1"))
      .expectError(ResourceNotFoundException.class)
        .verify();
  }
}
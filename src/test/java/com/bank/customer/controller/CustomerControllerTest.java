package com.bank.customer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerResponse;
import com.bank.customer.model.CustomerTypeEnum;
import com.bank.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CustomerControllerTest {

  @Mock
  private CustomerService service;

  @InjectMocks
  private CustomerController controller;

  private CustomerResponse response;
  private CustomerRequest request;
  private MockServerWebExchange exchange;

  private CustomerResponse createResponse() {
    CustomerResponse res = new CustomerResponse();
    res.setId("1");
    res.setFirstName("Giomar");
    res.setLastName("Limo");
    res.setCustomerType(CustomerTypeEnum.PERSONAL);
    return res;
  }

  private CustomerRequest createRequest() {
    CustomerRequest req = new CustomerRequest();
    req.setFirstName("Giomar");
    req.setCustomerType(CustomerTypeEnum.PERSONAL);
    return req;
  }

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/customers").build());

    response = createResponse();
    request = createRequest();
  }

  @Test
  void testCreateSuccess() {
    when(service.create(any())).thenReturn(Mono.just(response));

    StepVerifier.create(controller.create(Mono.just(request), exchange))
      .consumeNextWith(res -> {
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals("1", res.getBody().getId());
      })
        .verifyComplete();
  }

  @Test
  void testGetAll() {
    when(service.findAll()).thenReturn(Flux.just(response));

    StepVerifier.create(controller.getAll(exchange))
      .consumeNextWith(res -> assertEquals(HttpStatus.OK, res.getStatusCode()))
        .verifyComplete();
  }

  @Test
  void testGetByIdFound() {
    when(service.findById("1")).thenReturn(Mono.just(response));

    StepVerifier.create(controller.getById("1", exchange))
      .consumeNextWith(res -> assertEquals(HttpStatus.OK, res.getStatusCode()))
        .verifyComplete();
  }

  @Test
  void testGetByIdNotFound() {
    when(service.findById("99")).thenReturn(Mono.empty());

    StepVerifier.create(controller.getById("99", exchange))
      .consumeNextWith(res -> assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode()))
        .verifyComplete();
  }

  @Test
  void testUpdateSuccess() {
    when(service.update(any(), any())).thenReturn(Mono.just(response));

    StepVerifier.create(controller.update("1", Mono.just(request), exchange))
      .consumeNextWith(res -> assertEquals(HttpStatus.OK, res.getStatusCode()))
        .verifyComplete();
  }

  @Test
  void testDelete() {
    when(service.delete("1")).thenReturn(Mono.empty());

    StepVerifier.create(controller.delete("1", exchange))
      .consumeNextWith(res -> assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode()))
        .verifyComplete();
  }
}
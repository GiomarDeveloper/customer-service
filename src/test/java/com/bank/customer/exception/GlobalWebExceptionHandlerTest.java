package com.bank.customer.exception;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

class GlobalWebExceptionHandlerTest {

  private final GlobalWebExceptionHandler handler = new GlobalWebExceptionHandler();

  @Test
  void testHandleError() {
    Throwable error = new RuntimeException("Database down");
    MockServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/api/test").build()
    );

    StepVerifier.create(handler.handle(exchange, error))
        .verifyComplete();
  }
}
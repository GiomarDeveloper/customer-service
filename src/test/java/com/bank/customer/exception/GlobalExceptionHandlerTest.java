package com.bank.customer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void testHandleNotFound() {
    ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/api/test").build()
    );

    StepVerifier.create(handler.handleNotFound(ex, exchange))
      .consumeNextWith(response -> {
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().getMessage());
      })
        .verifyComplete();
  }

  @Test
  void testHandleValidationException() throws Exception {
    BindException bindException = new BindException(new Object(), "test");
    bindException.addError(new FieldError("objectName", "field", "must not be null"));

    Method method = DummyController.class.getMethod("dummyMethod", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    WebExchangeBindException ex = new WebExchangeBindException(parameter, bindException);

    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.post("/api/validate").build()
    );

    StepVerifier.create(handler.handleValidationException(ex, exchange))
      .consumeNextWith(response -> {
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getDetails());
      })
        .verifyComplete();
  }

  @Test
  void testHandleConstraintViolation() {
    // Simula una ConstraintViolationException (ej: validaciones de @NotBlank)
    @SuppressWarnings("unchecked")
    ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
    javax.validation.Path path = mock(javax.validation.Path.class);

    when(path.toString()).thenReturn("field");
    when(violation.getPropertyPath()).thenReturn(path);
    when(violation.getMessage()).thenReturn("must not be null");

    ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/api/constraint").build()
    );

    StepVerifier.create(handler.handleConstraintViolation(ex, exchange))
      .consumeNextWith(response -> {
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getDetails().containsKey("field"));
      })
        .verifyComplete();
  }

  class DummyController {
    public void dummyMethod(@org.springframework.web.bind.annotation.RequestBody String body) {
    }
  }
}
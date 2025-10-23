package com.bank.customer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @Test
  void testEqualsHashCodeAndSetters() {
    Instant now = Instant.now();
    ErrorResponse response1 = ErrorResponse.builder()
        .timestamp(now)
        .status(400)
        .error("Bad Request")
        .message("Invalid data")
        .path("/api/customer")
        .details(Map.of("field", "name"))
        .build();

    ErrorResponse response2 = new ErrorResponse();
    response2.setTimestamp(now);
    response2.setStatus(400);
    response2.setError("Bad Request");
    response2.setMessage("Invalid data");
    response2.setPath("/api/customer");
    response2.setDetails(Map.of("field", "name"));

    assertEquals(response1, response2);
    assertEquals(response1.hashCode(), response2.hashCode());
    assertTrue(response1.toString().contains("Bad Request"));
  }
}
package com.bank.customer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @Test
  void testErrorResponseFieldsAndMethods() {
    OffsetDateTime now = OffsetDateTime.now();
    ErrorResponse error = new ErrorResponse();
    error.setTimestamp(now);
    error.setStatus(404);
    error.setError("Not Found");
    error.setMessage("Customer not found");
    error.setPath("/api/customers/1");
    error.setDetails(Map.of("key", "value"));

    assertEquals(404, error.getStatus());
    assertEquals("Not Found", error.getError());
    assertEquals("Customer not found", error.getMessage());
    assertEquals("/api/customers/1", error.getPath());
    assertEquals("value", error.getDetails().get("key"));
    assertEquals(error, error);
    assertNotNull(error.toString());
    assertNotEquals(error.hashCode(), 0);
  }
}
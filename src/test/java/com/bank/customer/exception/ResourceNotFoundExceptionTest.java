package com.bank.customer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ResourceNotFoundExceptionTest {

  @Test
  void testExceptionMessage() {
    ResourceNotFoundException ex = new ResourceNotFoundException("Customer not found");
    assertEquals("Customer not found", ex.getMessage());
  }
}
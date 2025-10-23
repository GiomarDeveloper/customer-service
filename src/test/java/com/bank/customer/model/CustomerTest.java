package com.bank.customer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class CustomerTest {

  @Test
  void testCustomerBuilderAndGetters() {
    Instant now = Instant.now();

    Customer customer = Customer.builder()
        .id("1")
        .documentType("DNI")
        .documentNumber("12345678")
        .firstName("Giomar")
        .lastName("Limo")
        .email("test@example.com")
        .phone("+51987654321")
        .customerType("PERSONAL")
        .createdAt(now)
        .build();

    assertEquals("1", customer.getId());
    assertEquals("DNI", customer.getDocumentType());
    assertEquals("PERSONAL", customer.getCustomerType());
    assertEquals(now, customer.getCreatedAt());
    assertNotNull(customer.toString());
  }

  @Test
  void testEqualsHashCodeAndCanEqual() {
    Customer a = new Customer();
    a.setId("1");
    a.setDocumentNumber("123");

    Customer b = new Customer();
    b.setId("1");
    b.setDocumentNumber("123");

    Customer c = new Customer();
    c.setId("2");

    assertEquals(a, b);
    assertNotEquals(a, c);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(a.hashCode(), c.hashCode());
    assertTrue(a.canEqual(b));
  }

  @Test
  void testToStringAndBuilder() {
    Customer c = Customer.builder()
        .id("1")
        .documentNumber("123")
        .firstName("Giomar")
        .build();
    assertNotNull(c.toString());
  }
}
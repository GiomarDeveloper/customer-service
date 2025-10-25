package com.bank.customer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CustomerRequestTest {

  @Test
  void testSettersAndGetters() {
    CustomerRequest req = new CustomerRequest();
    req.setDocumentType("DNI");
    req.setDocumentNumber("12345678");
    req.setFirstName("Giomar");
    req.setLastName("Limo");
    req.setEmail("giomar@test.com");
    req.setPhone("999999999");
    req.setCustomerType(CustomerTypeEnum.PERSONAL);

    assertEquals("DNI", req.getDocumentType());
    assertEquals("12345678", req.getDocumentNumber());
    assertEquals("Giomar", req.getFirstName());
    assertEquals("Limo", req.getLastName());
    assertEquals("giomar@test.com", req.getEmail());
    assertEquals("999999999", req.getPhone());
    assertEquals("PERSONAL", req.getCustomerType());
  }

  @Test
  void testEqualsAndHashCode() {
    CustomerRequest a = new CustomerRequest();
    a.setDocumentNumber("123");

    CustomerRequest b = new CustomerRequest();
    b.setDocumentNumber("123");

    CustomerRequest c = new CustomerRequest();
    c.setDocumentNumber("999");

    assertEquals(a, b);
    assertNotEquals(a, c);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(a.hashCode(), c.hashCode());
  }

  @Test
  void testToStringAndConstructor() {
    CustomerRequest req = new CustomerRequest(
      "DNI", "123", "Giomar", CustomerTypeEnum.PERSONAL
    );
    assertNotNull(req.toString());
  }
}
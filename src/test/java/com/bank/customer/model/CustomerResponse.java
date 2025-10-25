package com.bank.customer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class CustomerResponseTest {

  @Test
  void testGettersAndSetters() {
    CustomerResponse res = new CustomerResponse();
    res.setId("1");
    res.setDocumentType("DNI");
    res.setDocumentNumber("123");
    res.setFirstName("Giomar");
    res.setLastName("Limo");
    res.setEmail("giomar@test.com");
    res.setPhone("999999999");
    res.setCustomerType(CustomerTypeEnum.PERSONAL);
    res.setCreatedAt(OffsetDateTime.now());

    assertEquals("1", res.getId());
    assertEquals("DNI", res.getDocumentType());
    assertEquals("123", res.getDocumentNumber());
    assertEquals("Giomar", res.getFirstName());
    assertEquals("Limo", res.getLastName());
    assertEquals("giomar@test.com", res.getEmail());
    assertEquals("999999999", res.getPhone());
    assertEquals(CustomerTypeEnum.PERSONAL, res.getCustomerType());
    assertNotNull(res.getCreatedAt());
    assertNotNull(res.toString());
  }

  @Test
  void testEqualsAndHashCode() {
    CustomerResponse a = new CustomerResponse();
    a.setId("1");
    a.setFirstName("Giomar");

    CustomerResponse b = new CustomerResponse();
    b.setId("1");
    b.setFirstName("Giomar");

    CustomerResponse c = new CustomerResponse();
    c.setId("2");

    assertEquals(a, b);
    assertNotEquals(a, c);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(a.hashCode(), c.hashCode());
  }
}
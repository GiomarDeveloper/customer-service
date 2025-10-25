package com.bank.customer.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerTypeEnum;
import java.time.Instant;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

  private final CustomerMapper mapper = new CustomerMapperImpl(); // MapStruct genera esta clase

  @Test
  void testToEntity() {
    CustomerRequest request = new CustomerRequest();
    request.setDocumentType("DNI");
    request.setDocumentNumber("12345678");
    request.setFirstName("Giomar");
    request.setLastName("Limo");
    request.setCustomerType(CustomerTypeEnum.PERSONAL);

    Customer entity = mapper.toEntity(request);

    assertNotNull(entity);
    assertEquals("DNI", entity.getDocumentType());
  }

  @Test
  void testMapInstantToOffsetDateTime() {
    Instant now = Instant.now();
    OffsetDateTime result = mapper.map(now);
    assertEquals(now.atOffset(java.time.ZoneOffset.UTC), result);
  }

  @Test
  void testMapInstantNull() {
    assertNull(mapper.map(null));
  }
}
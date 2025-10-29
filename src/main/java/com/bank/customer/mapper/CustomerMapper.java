package com.bank.customer.mapper;

import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir entre entidades Customer y DTOs.
 * Utiliza MapStruct para el mapeo automático de objetos.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper {

  /**
   * Convierte CustomerRequest a entidad Customer.
   *
   * @param dto el DTO de solicitud de cliente
   * @return la entidad Customer
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Customer toEntity(CustomerRequest dto);

  /**
   * Convierte entidad Customer a CustomerResponse.
   *
   * @param entity la entidad Customer
   * @return el DTO de respuesta de cliente
   */
  CustomerResponse toResponse(Customer entity);

  /**
   * Convierte Instant a OffsetDateTime.
   *
   * @param instant el tiempo instantáneo
   * @return OffsetDateTime en UTC
   */
  default OffsetDateTime map(Instant instant) {
    return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
  }
}
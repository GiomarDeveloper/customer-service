package com.bank.customer.repository;

import com.bank.customer.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para operaciones de base de datos de Customer.
 * Extiende ReactiveMongoRepository para operaciones CRUD reactivas.
 */
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

  /**
   * Busca un cliente por número de documento.
   *
   * @param documentNumber el número de documento del cliente
   * @return Mono que emite el Customer encontrado o vacío si no existe
   */
  Mono<Customer> findByDocumentNumber(String documentNumber);
}
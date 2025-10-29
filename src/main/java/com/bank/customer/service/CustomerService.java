package com.bank.customer.service;

import com.bank.customer.model.ConsolidatedSummary;
import com.bank.customer.model.CustomerMonthlySummary;
import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerResponse;
import com.bank.customer.model.ProductReportRequest;
import com.bank.customer.model.ProductReportResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz de servicio para operaciones de gestión de clientes.
 * Define los contratos para las operaciones CRUD y de negocio.
 */
public interface CustomerService {
  /**
   * Obtiene todos los clientes del sistema.
   *
   * @return Flux de CustomerResponse con todos los clientes
   */
  Flux<CustomerResponse> findAll();

  /**
   * Busca un cliente por su ID.
   *
   * @param id el ID del cliente
   * @return Mono con el CustomerResponse encontrado
   */
  Mono<CustomerResponse> findById(String id);

  /**
   * Busca un cliente por número de documento.
   *
   * @param documentNumber el número de documento del cliente
   * @return Mono con el CustomerResponse encontrado
   */
  Mono<CustomerResponse> findByDocumentNumber(String documentNumber);

  /**
   * Crea un nuevo cliente en el sistema.
   *
   * @param request los datos del cliente a crear
   * @return Mono con el CustomerResponse creado
   */
  Mono<CustomerResponse> create(CustomerRequest request);

  /**
   * Actualiza un cliente existente.
   *
   * @param id el ID del cliente a actualizar
   * @param request los nuevos datos del cliente
   * @return Mono con el CustomerResponse actualizado
   */
  Mono<CustomerResponse> update(String id, CustomerRequest request);

  /**
   * Elimina un cliente del sistema.
   *
   * @param id el ID del cliente a eliminar
   * @return Mono vacío que indica completación
   */
  Mono<Void> delete(String id);

  /**
   * Genera un resumen mensual para un cliente.
   *
   * @param customerId el ID del cliente
   * @return Mono con el resumen mensual del cliente
   */
  Mono<CustomerMonthlySummary> generateMonthlySummary(String customerId);

  /**
   * Obtiene un resumen consolidado de productos para un cliente.
   *
   * @param customerId el ID del cliente
   * @return Mono con el resumen consolidado
   */
  Mono<ConsolidatedSummary> getConsolidatedSummary(String customerId);

  /**
   * Genera un reporte de productos para un período específico.
   *
   * @param request los parámetros del reporte (fechas, etc.)
   * @return Mono con el reporte de productos generado
   */
  Mono<ProductReportResponse> generateProductReport(ProductReportRequest request);
}
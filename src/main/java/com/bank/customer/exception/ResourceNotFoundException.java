package com.bank.customer.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 * Generalmente se usa para recursos como clientes, cuentas, etc.
 */
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Constructor con mensaje de error.
   *
   * @param message el mensaje descriptivo del error
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
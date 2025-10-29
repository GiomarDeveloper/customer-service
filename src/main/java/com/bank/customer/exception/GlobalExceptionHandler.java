package com.bank.customer.exception;

import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Manejador global de excepciones para el microservicio de clientes.
 * Captura y procesa todas las excepciones no manejadas en los controladores.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Maneja excepciones cuando no se encuentra un recurso.
   *
   * @param ex la excepción de recurso no encontrado
   * @param exchange el intercambio del servidor web
   * @return respuesta de error con estado 404
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleNotFound(ResourceNotFoundException ex,
                                                            ServerWebExchange exchange) {
    log.warn("Resource not found: {}", ex.getMessage());

    ErrorResponse response = ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage())
        .path(exchange.getRequest().getPath().value())
        .build();

    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(response));
  }

  /**
   * Maneja excepciones de validación de datos de entrada.
   *
   * @param ex la excepción de validación
   * @param exchange el intercambio del servidor web
   * @return respuesta de error con estado 400
   */
  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleValidationException(WebExchangeBindException ex,
                                                                       ServerWebExchange exchange) {
    log.warn("Validation error: {}", ex.getMessage());

    Map<String, String> fieldErrors = ex.getFieldErrors().stream()
        .collect(Collectors.toMap(
          fieldError -> fieldError.getField(),
          fieldError -> fieldError.getDefaultMessage(),
          (msg1, msg2) -> msg1
      ));

    ErrorResponse response = ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message("Validation error in request fields")
        .path(exchange.getRequest().getPath().value())
        .details(fieldErrors)
        .build();

    return Mono.just(ResponseEntity.badRequest().body(response));
  }

  /**
   * Maneja violaciones de constraints de validación.
   *
   * @param ex la excepción de violación de constraint
   * @param exchange el intercambio del servidor web
   * @return respuesta de error con estado 400
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleConstraintViolation(
      ConstraintViolationException ex, ServerWebExchange exchange) {
    log.warn("Constraint violation: {}", ex.getMessage());

    Map<String, String> fieldErrors = ex.getConstraintViolations().stream()
        .collect(Collectors.toMap(
          violation -> violation.getPropertyPath().toString(),
          violation -> violation.getMessage(),
          (msg1, msg2) -> msg1
      ));

    ErrorResponse response = ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message("Constraint violation in request")
        .path(exchange.getRequest().getPath().value())
        .details(fieldErrors)
        .build();

    return Mono.just(ResponseEntity.badRequest().body(response));
  }

  /**
   * Maneja excepciones generales no capturadas por otros manejadores.
   *
   * @param ex la excepción general
   * @param exchange el intercambio del servidor web
   * @return respuesta de error con estado 500
   */
  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ErrorResponse>> handleGeneralError(Exception ex,
                                                                ServerWebExchange exchange) {
    log.error("Unexpected error: ", ex);

    ErrorResponse response = ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message("An unexpected error occurred")
        .path(exchange.getRequest().getPath().value())
        .build();

    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
  }
}
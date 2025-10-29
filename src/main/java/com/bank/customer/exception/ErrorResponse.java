package com.bank.customer.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo estándar para respuestas de error en la API.
 * Proporciona una estructura consistente para manejar errores.
 */
@Data
@Builder
@Schema(description = "Respuesta de error estándar de la API")
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
  @Builder.Default
  private Instant timestamp = Instant.now();
  private int status;
  private String error;
  private String message;
  private String path;
  private Map<String, String> details;
}
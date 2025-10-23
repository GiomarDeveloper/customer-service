package com.bank.customer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-2) // se ejecuta antes que otros handlers
public class GlobalWebExceptionHandler implements WebExceptionHandler {

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    log.error("Global reactive error: ", ex);

    ErrorResponse response = ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("MongoDB Connection Error")
        .message("Database service unavailable: " + ex.getMessage())
        .path(exchange.getRequest().getPath().value())
        .build();

    exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    byte[] bytes = response.toString().getBytes();
    return exchange.getResponse()
      .writeWith(Mono.just(exchange.getResponse()
        .bufferFactory().wrap(bytes)));
  }
}
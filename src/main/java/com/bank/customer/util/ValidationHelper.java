package com.bank.customer.util;

import com.bank.customer.model.CustomerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

/**
 * Utilidad para validación asíncrona de objetos.
 * Proporciona métodos para validar DTOs de forma reactiva.
 */
@Component
@RequiredArgsConstructor
public class ValidationHelper {

  private final Validator validator;

  /**
   * Valida un CustomerRequest de forma asíncrona.
   *
   * @param request el CustomerRequest a validar
   * @return Mono con el request validado o error de validación
   */
  public Mono<CustomerRequest> validateAsync(CustomerRequest request) {
    return Mono.fromCallable(() -> {
      BindingResult bindingResult = new BeanPropertyBindingResult(request, "customerRequest");
      validator.validate(request, bindingResult);
      if (bindingResult.hasErrors()) {
        throw new WebExchangeBindException(null, bindingResult);
      }
      return request;
    });
  }
}
package com.bank.customer.util;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.bank.customer.model.CustomerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Validator;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.test.StepVerifier;

class ValidationHelperTest {

  private Validator validator;
  private ValidationHelper helper;

  @BeforeEach
  void setup() {
    validator = mock(Validator.class);
    helper = new ValidationHelper(validator);
  }

  @Test
  void testValidateAsyncSuccess() {
    CustomerRequest request = new CustomerRequest();

    StepVerifier.create(helper.validateAsync(request))
      .expectNext(request)
        .verifyComplete();

    verify(validator).validate(eq(request), any());
  }

  @Test
  void testValidateAsyncFailure() {
    Validator failingValidator = mock(Validator.class);
    doAnswer(invocation -> {
      var errors = invocation.getArgument(1, org.springframework.validation.BindingResult.class);
      errors.reject("error");
      return null;
    }).when(failingValidator).validate(any(), any());

    ValidationHelper helper2 = new ValidationHelper(failingValidator);
    CustomerRequest request = new CustomerRequest();

    StepVerifier.create(helper2.validateAsync(request))
      .expectError(WebExchangeBindException.class)
        .verify();
  }
}
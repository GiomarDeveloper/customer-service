package com.bank.customer.util;

import com.bank.customer.model.CustomerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.support.WebExchangeBindException;

@Component
@RequiredArgsConstructor
public class ValidationHelper {

    private final Validator validator;

    public void validate(CustomerRequest request) {
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "customerRequest");
        validator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new WebExchangeBindException(null, bindingResult);
        }
    }
}
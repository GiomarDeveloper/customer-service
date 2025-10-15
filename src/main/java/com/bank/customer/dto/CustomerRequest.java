package com.bank.customer.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class CustomerRequest {

    @NotBlank(message = "El tipo de documento es obligatorio.")
    private String documentType;

    @NotBlank(message = "El número de documento es obligatorio.")
    private String documentNumber;

    @NotBlank(message = "El nombre es obligatorio.")
    private String firstName;

    private String lastName;

    @Email(message = "El correo electrónico no es válido.")
    private String email;

    private String phone;

    @NotBlank(message = "El tipo de cliente es obligatorio.")
    private String customerType;
}
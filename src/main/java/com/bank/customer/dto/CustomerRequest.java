package com.bank.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class CustomerRequest {

    @Schema(description = "Tipo de documento del cliente (DNI, CEX, PASSPORT)", example = "DNI")
    @NotBlank(message = "El tipo de documento es obligatorio.")
    private String documentType;

    @Schema(description = "Número de documento del cliente", example = "12345678")
    @NotBlank(message = "El número de documento es obligatorio.")
    private String documentNumber;

    @Schema(description = "Nombre del cliente", example = "Juan")
    @NotBlank(message = "El nombre es obligatorio.")
    private String firstName;

    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String lastName;

    @Schema(description = "Correo electrónico del cliente", example = "juan.perez@gmail.com")
    @Email(message = "El correo electrónico no es válido.")
    private String email;

    @Schema(description = "Teléfono del cliente", example = "987654321")
    private String phone;

    @Schema(description = "Tipo de cliente (PERSONAL o BUSINESS)", example = "PERSONAL")
    @NotBlank(message = "El tipo de cliente es obligatorio.")
    private String customerType;
}
package com.bank.customer.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;

    @NotBlank(message = "documentType is required")
    @Size(min = 2, max = 20, message = "documentType must be between 2 and 20 characters")
    @Pattern(regexp = "DNI|CEX|PASSPORT", message = "documentType must be DNI, CEX or PASSPORT")
    private String documentType;

    @NotBlank(message = "documentNumber is required")
    @Size(min = 3, max = 20, message = "documentNumber must be between 3 and 20 characters")
    private String documentNumber;

    @NotBlank(message = "firstName is required")
    @Size(min = 2, max = 50, message = "firstName must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "lastName must be between 2 and 50 characters")
    private String lastName;

    @Email(message = "email must be valid")
    @Size(max = 100, message = "email must not exceed 100 characters")
    private String email;

    @Size(min = 7, max = 15, message = "phone must be between 7 and 15 characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "phone must contain only numbers, spaces, and valid characters")
    private String phone;

    @NotBlank(message = "customerType is required")
    @Size(min = 3, max = 20, message = "customerType must be between 3 and 20 characters")
    @Pattern(regexp = "PERSONAL|EMPRESARIAL", message = "customerType must be PERSONAL, EMPRESARIAL")
    private String customerType;

    private Instant createdAt;
}
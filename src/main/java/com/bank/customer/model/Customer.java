package com.bank.customer.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;

    @NotBlank
    private String documentType;   // DNI, CEX, PASSPORT

    @NotBlank
    private String documentNumber;

    @NotBlank
    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    @NotBlank
    private String customerType;

    private Instant createdAt;
}

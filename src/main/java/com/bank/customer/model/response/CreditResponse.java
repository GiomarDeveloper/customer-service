package com.bank.customer.model.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para información de créditos.
 * Representa los datos de un producto de crédito.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditResponse {
  private String id;
  private String creditNumber;
  private String creditType;
  private String customerId;
  private Double amount;
  private Double outstandingBalance;
  private Double interestRate;
  private Double creditLimit;
  private Double availableCredit;
  private Integer termMonths;
  private Double monthlyPayment;
  private Integer remainingPayments;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
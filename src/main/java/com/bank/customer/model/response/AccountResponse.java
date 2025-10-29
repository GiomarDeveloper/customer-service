package com.bank.customer.model.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para información de cuentas.
 * Representa los datos de una cuenta bancaria.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
  private String id;
  private String accountNumber;
  private String accountType;
  private String customerId;
  private Double balance;
  private Double maintenanceFee;
  private Integer monthlyTransactionLimit;
  private Integer currentMonthTransactions;
  private Integer fixedTermDay;
  private List<Holder> holders;
  private List<Signatory> signatories;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Double minimumDailyAverage;
  private Integer freeTransactionLimit;
  private Double excessTransactionFee;

  /**
   * Representa un titular de cuenta.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Holder {
    private String customerId;
    private String name;
    private String relationship;
  }

  /**
   * Representa un firmante autorizado de cuenta.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Signatory {
    private String customerId;
    private String name;
    private String relationship;
  }
}
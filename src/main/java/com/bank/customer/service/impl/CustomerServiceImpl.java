package com.bank.customer.service.impl;

import com.bank.customer.client.AccountServiceClient;
import com.bank.customer.client.CreditServiceClient;
import com.bank.customer.exception.ResourceNotFoundException;
import com.bank.customer.mapper.CustomerMapper;
import com.bank.customer.model.AccountSummary;
import com.bank.customer.model.CreditSummary;
import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerMonthlySummary;
import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerResponse;
import com.bank.customer.repository.CustomerRepository;
import com.bank.customer.service.CustomerService;
import com.bank.customer.util.ValidationHelper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

  private final CustomerMapper mapper;
  private final ValidationHelper validationHelper;
  private final CustomerRepository repo;
  private final AccountServiceClient accountServiceClient;
  private final CreditServiceClient creditServiceClient;

  @Override
  public Flux<CustomerResponse> findAll() {
    return repo.findAll()
      .map(mapper::toResponse)
      .doOnComplete(() -> log.info("Retrieved all customers successfully"))
      .doOnError(error -> log.error("Error retrieving customers: {}", error.getMessage(), error));
  }

  @Override
  public Mono<CustomerResponse> findById(String id) {
    return repo.findById(id)
      .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found with id: " + id)))
      .map(mapper::toResponse)
      .doOnSuccess(response -> log.info("Retrieved customer successfully with id: {}", id))
      .doOnError(error -> log.error("Error retrieving customer with id {}: {}", id, error.getMessage(), error));
  }

  @Override
  public Mono<CustomerResponse> findByDocumentNumber(String documentNumber) {
    return repo.findByDocumentNumber(documentNumber)
      .switchIfEmpty(Mono.error(new ResourceNotFoundException(
        "Customer not found with document number: " + documentNumber)))
      .map(mapper::toResponse)
      .doOnSuccess(response -> log.info("Retrieved customer successfully with document: {}", documentNumber))
      .doOnError(error -> log.error("Error retrieving customer with document {}: {}", documentNumber, error.getMessage(), error));
  }

  @Override
  public Mono<CustomerResponse> create(CustomerRequest request) {
    return validationHelper.validateAsync(request)
      .map(mapper::toEntity)
      .flatMap(customer -> repo.findByDocumentNumber(customer.getDocumentNumber())
        .flatMap(existing -> Mono.<Customer>error(
          new RuntimeException("Customer with document number already exists")))
        .switchIfEmpty(Mono.defer(() -> {
          customer.setCreatedAt(Instant.now());
          return repo.save(customer);
        }))
      )
      .map(mapper::toResponse)
      .doOnSuccess(response -> log.info("Customer created successfully with id: {}", response.getId()))
      .doOnError(error -> log.error("Error creating customer: {}", error.getMessage(), error));
  }


  @Override
  public Mono<CustomerResponse> update(String id, CustomerRequest request) {
    return validationHelper.validateAsync(request)
      .map(mapper::toEntity)
      .flatMap(customer -> repo.findById(id)
        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found with ID " + id)))
        .flatMap(existing -> {
          existing.setFirstName(customer.getFirstName());
          existing.setLastName(customer.getLastName());
          existing.setEmail(customer.getEmail());
          existing.setPhone(customer.getPhone());
          existing.setCustomerType(customer.getCustomerType());
          existing.setDocumentNumber(customer.getDocumentNumber());
          return repo.save(existing);
        }))
      .map(mapper::toResponse)
      .doOnSuccess(response -> log.info("Customer updated successfully with id: {}", id))
      .doOnError(error -> log.error("Error updating customer with id {}: {}", id, error.getMessage(), error));
  }

  @Override
  public Mono<Void> delete(String id) {
    return repo.findById(id)
      .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found with ID " + id)))
      .flatMap(repo::delete)
      .doOnSuccess(unused -> log.info("Customer deleted successfully with id: {}", id))
      .doOnError(error -> log.error("Error deleting customer with id {}: {}", id, error.getMessage(), error));
  }

  public Mono<CustomerMonthlySummary> generateMonthlySummary(String customerId) {
    log.info("Generating monthly summary for customer: {}", customerId);

    return Mono.zip(
      accountServiceClient.getCustomerAccountsWithDailyBalances(customerId).collectList(),
      creditServiceClient.getCustomerCreditsWithDailyBalances(customerId).collectList()
    ).map(tuple -> {
      List<AccountSummary> accounts = tuple.getT1();
      List<CreditSummary> credits = tuple.getT2();

      // Calcular promedio total
      double totalDailyAverage = calculateTotalDailyAverage(accounts, credits);

      // Crear objeto sin Builder
      CustomerMonthlySummary summary = new CustomerMonthlySummary();
      summary.setCustomerId(customerId);
      summary.setPeriod(getCurrentPeriod());
      summary.setTotalDailyAverage(totalDailyAverage);
      summary.setAccountSummaries(accounts);
      summary.setCreditSummaries(credits);
      summary.setGeneratedAt(OffsetDateTime.now());

      return summary;
    });
  }

  private double calculateTotalDailyAverage(List<AccountSummary> accounts, List<CreditSummary> credits) {
    double accountsSum = accounts.stream()
      .mapToDouble(AccountSummary::getDailyAverage)
      .sum();

    double creditsSum = credits.stream()
      .mapToDouble(CreditSummary::getDailyAverage)
      .sum();

    return accountsSum + creditsSum;
  }

  private String getCurrentPeriod() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"))).toUpperCase();
  }
}
package com.bank.customer.service.impl;

import com.bank.customer.client.AccountServiceClient;
import com.bank.customer.client.CreditServiceClient;
import com.bank.customer.client.TransactionServiceClient;
import com.bank.customer.exception.ResourceNotFoundException;
import com.bank.customer.mapper.CustomerMapper;
import com.bank.customer.model.AccountDetail;
import com.bank.customer.model.AccountMetrics;
import com.bank.customer.model.AccountSummary;
import com.bank.customer.model.AccountTypeEnum;
import com.bank.customer.model.ConsolidatedSummary;
import com.bank.customer.model.CreditDetail;
import com.bank.customer.model.CreditMetrics;
import com.bank.customer.model.CreditSummary;
import com.bank.customer.model.CreditTypeEnum;
import com.bank.customer.model.Customer;
import com.bank.customer.model.CustomerMetrics;
import com.bank.customer.model.CustomerMonthlySummary;
import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerResponse;
import com.bank.customer.model.CustomerTypeEnum;
import com.bank.customer.model.ProductReportRequest;
import com.bank.customer.model.ProductReportResponse;
import com.bank.customer.model.ProductsOverview;
import com.bank.customer.model.ReportSummary;
import com.bank.customer.model.TransactionDetail;
import com.bank.customer.model.response.AccountResponse;
import com.bank.customer.model.response.CreditResponse;
import com.bank.customer.model.response.TransactionResponse;
import com.bank.customer.repository.CustomerRepository;
import com.bank.customer.service.CustomerService;
import com.bank.customer.util.ValidationHelper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
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
  private final TransactionServiceClient transactionServiceClient;

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

  @Override
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

  @Override
  public Mono<ConsolidatedSummary> getConsolidatedSummary(String customerId) {
    log.info("Generating consolidated summary for customer: {}", customerId);

    return repo.findById(customerId)
      .switchIfEmpty(Mono.error(new RuntimeException("Customer not found with id: " + customerId)))
      .flatMap(customer ->
        Mono.zip(
          accountServiceClient.getCustomerAccounts(customerId).collectList(),
          creditServiceClient.getCustomerCredits(customerId).collectList(),
          transactionServiceClient.getRecentTransactions(customerId).collectList()
        ).map(tuple -> buildConsolidatedSummary(customer, tuple.getT1(), tuple.getT2(), tuple.getT3()))
      );
  }

  @Override
  public Mono<ProductReportResponse> generateProductReport(ProductReportRequest request) {
    log.info("Generating product report from {} to {}", request.getStartDate(), request.getEndDate());

    String startDateStr = request.getStartDate().toString();
    String endDateStr = request.getEndDate().toString();

    return Mono.zip(
      repo.findAll().collectList(),
      accountServiceClient.getAllAccounts().collectList(),
      creditServiceClient.getAllCredits().collectList(),
      transactionServiceClient.getTransactionsByDateRange(
        startDateStr, endDateStr
      ).collectList()
    ).map(tuple -> buildProductReport(request, tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4()));
  }

  private ProductReportResponse buildProductReport(ProductReportRequest request,
                                                   List<Customer> customers,
                                                   List<AccountResponse> accounts,
                                                   List<CreditResponse> credits,
                                                   List<TransactionResponse> transactions) {

    ProductReportResponse response = new ProductReportResponse();
    response.setPeriod(buildPeriodString(request));
    response.setGeneratedAt(OffsetDateTime.now());
    response.setSummary(buildReportSummary(customers, accounts, credits, transactions));
    response.setAccountMetrics(buildAccountMetrics(accounts));
    response.setCreditMetrics(buildCreditMetrics(credits));
    response.setCustomerMetrics(buildCustomerMetrics(customers, accounts, credits));

    return response;
  }

  private ReportSummary buildReportSummary(List<Customer> customers,
                                           List<AccountResponse> accounts,
                                           List<CreditResponse> credits,
                                           List<TransactionResponse> transactions) {

    long activeCustomers = customers.size();

    double totalBalance = accounts.stream()
      .mapToDouble(AccountResponse::getBalance)
      .sum();

    ReportSummary summary = new ReportSummary();
    summary.setTotalCustomers(customers.size());
    summary.setActiveCustomers((int) activeCustomers);
    summary.setTotalAccounts(accounts.size());
    summary.setTotalCredits(credits.size());
    summary.setTotalBalance(totalBalance);
    summary.setTotalTransactions(transactions.size());

    return summary;
  }

  private AccountMetrics buildAccountMetrics(List<AccountResponse> accounts) {
    Map<String, Long> accountsByTypeLong = accounts.stream()
      .collect(Collectors.groupingBy(AccountResponse::getAccountType, Collectors.counting()));

    double averageBalance = accounts.stream()
      .mapToDouble(AccountResponse::getBalance)
      .average()
      .orElse(0.0);

    AccountMetrics metrics = new AccountMetrics();
    metrics.setTotalAccounts(accounts.size());
    metrics.setByType(convertMapLongToInt(accountsByTypeLong));
    metrics.setAverageBalance(averageBalance);
    metrics.setNewAccounts(0);

    return metrics;
  }

  private CreditMetrics buildCreditMetrics(List<CreditResponse> credits) {
    Map<String, Long> creditsByTypeLong = credits.stream()
      .collect(Collectors.groupingBy(CreditResponse::getCreditType, Collectors.counting()));

    double totalOutstanding = credits.stream()
      .mapToDouble(CreditResponse::getOutstandingBalance)
      .sum();

    double averageInterestRate = credits.stream()
      .mapToDouble(CreditResponse::getInterestRate)
      .average()
      .orElse(0.0);

    CreditMetrics metrics = new CreditMetrics();
    metrics.setTotalCredits(credits.size());
    metrics.setByType(convertMapLongToInt(creditsByTypeLong));
    metrics.setTotalOutstanding(totalOutstanding);
    metrics.setAverageInterestRate(averageInterestRate);

    return metrics;
  }

  private CustomerMetrics buildCustomerMetrics(List<Customer> customers,
                                               List<AccountResponse> accounts,
                                               List<CreditResponse> credits) {

    Map<String, Long> customersByTypeLong = customers.stream()
      .collect(Collectors.groupingBy(Customer::getCustomerType, Collectors.counting()));

    // Calcular promedio de productos por cliente
    Map<String, Long> accountsPerCustomer = accounts.stream()
      .collect(Collectors.groupingBy(AccountResponse::getCustomerId, Collectors.counting()));

    Map<String, Long> creditsPerCustomer = credits.stream()
      .collect(Collectors.groupingBy(CreditResponse::getCustomerId, Collectors.counting()));

    double avgProducts = customers.stream()
      .mapToDouble(customer ->
        accountsPerCustomer.getOrDefault(customer.getId(), 0L) +
          creditsPerCustomer.getOrDefault(customer.getId(), 0L)
      )
      .average()
      .orElse(0.0);

    CustomerMetrics metrics = new CustomerMetrics();
    metrics.setTotalCustomers(customers.size());
    metrics.setByType(convertMapLongToInt(customersByTypeLong));
    metrics.setAverageProductsPerCustomer(avgProducts);

    return metrics;
  }

  private String buildPeriodString(ProductReportRequest request) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    return request.getStartDate() + " - " + request.getEndDate();
  }

  private Map<String, Integer> convertMapLongToInt(Map<String, Long> longMap) {
    return longMap.entrySet().stream()
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        entry -> entry.getValue().intValue()
      ));
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

  private ConsolidatedSummary buildConsolidatedSummary(Customer customer,
                                                       List<AccountResponse> accounts,
                                                       List<CreditResponse> credits,
                                                       List<TransactionResponse> transactions) {

    Double totalBalance = calculateTotalBalance(accounts, credits);

    ConsolidatedSummary consolidatedSummary = new ConsolidatedSummary();
    consolidatedSummary.setCustomer(mapToCustomerResponse(customer));
    consolidatedSummary.setSummaryDate(OffsetDateTime.now());
    consolidatedSummary.setTotalBalance(totalBalance);
    consolidatedSummary.setProductsOverview(buildProductsOverview(accounts, credits, transactions));
    consolidatedSummary.setAccounts(mapToAccountDetails(accounts));
    consolidatedSummary.setCredits(mapToCreditDetails(credits));
    consolidatedSummary.setRecentTransactions(mapToTransactionDetails(transactions));

    return consolidatedSummary;
  }


  private Double calculateTotalBalance(List<AccountResponse> accounts, List<CreditResponse> credits) {
    double accountBalance = accounts.stream()
      .filter(account -> "ACTIVO".equals(account.getStatus()))
      .mapToDouble(AccountResponse::getBalance)
      .sum();

    double creditBalance = credits.stream()
      .filter(credit -> "ACTIVO".equals(credit.getStatus()))
      .mapToDouble(credit -> {
        // Para tarjetas de crédito, el availableCredit representa el saldo disponible
        if ("TARJETA_CREDITO".equals(credit.getCreditType())) {
          return credit.getCreditLimit() - credit.getAvailableCredit();
        } else {
          // Para préstamos, el outstandingBalance es la deuda
          return -credit.getOutstandingBalance();
        }
      })
      .sum();

    return accountBalance + creditBalance;
  }

  private ProductsOverview buildProductsOverview(List<AccountResponse> accounts,
                                                 List<CreditResponse> credits,
                                                 List<TransactionResponse> transactions) {
    ProductsOverview productsOverview = new ProductsOverview();
    productsOverview.setTotalAccounts(accounts.size());
    productsOverview.setTotalCredits(credits.size());
    productsOverview.setTotalTransactions(transactions.size());
    productsOverview.setActiveAccounts((int) accounts.stream().filter(acc -> "ACTIVO".equals(acc.getStatus())).count());
    productsOverview.setActiveCredits((int) credits.stream().filter(cred -> "ACTIVO".equals(cred.getStatus())).count());

    return productsOverview;
  }

  private List<AccountDetail> mapToAccountDetails(List<AccountResponse> accounts) {
    return accounts.stream()
      .map(this::mapToAccountDetail)
      .collect(Collectors.toList());
  }

  private AccountDetail mapToAccountDetail(AccountResponse account) {
    AccountDetail accountDetail = new AccountDetail();
    accountDetail.setId(account.getId());
    accountDetail.setAccountNumber(account.getAccountNumber());
    accountDetail.setAccountType(AccountTypeEnum.valueOf(account.getAccountType()));
    accountDetail.setCurrentBalance(account.getBalance());
    accountDetail.setStatus(account.getStatus());

    if (account.getCreatedAt() != null) {
      accountDetail.setOpenedDate(account.getCreatedAt().atOffset(ZoneOffset.UTC));
    }

    return accountDetail;
  }

  private List<CreditDetail> mapToCreditDetails(List<CreditResponse> credits) {
    return credits.stream()
      .map(this::mapToCreditDetail)
      .collect(Collectors.toList());
  }

  private CreditDetail mapToCreditDetail(CreditResponse credit) {
    CreditDetail creditDetail = new CreditDetail();
    creditDetail.setId(credit.getId());
    creditDetail.setCreditNumber(credit.getCreditNumber());
    creditDetail.setCreditType(CreditTypeEnum.valueOf(credit.getCreditType()));
    creditDetail.setCurrentBalance("TARJETA_CREDITO".equals(credit.getCreditType()) ?
      credit.getCreditLimit() - credit.getAvailableCredit() :
      -credit.getOutstandingBalance());
    creditDetail.setCreditLimit(credit.getCreditLimit());
    creditDetail.setAvailableCredit(credit.getAvailableCredit());
    creditDetail.setInterestRate(credit.getInterestRate());
    creditDetail.setStatus(credit.getStatus());

    // SOLO estos campos están definidos en el schema
    // Los demás campos (totalAmount, outstandingBalance, monthlyPayment, etc.) NO existen en CreditDetail

    return creditDetail;
  }

  private List<TransactionDetail> mapToTransactionDetails(List<TransactionResponse> transactions) {
    return transactions.stream()
      .map(this::mapToTransactionDetail)
      .collect(Collectors.toList());
  }

  private TransactionDetail mapToTransactionDetail(TransactionResponse transaction) {
    TransactionDetail transactionDetail = new TransactionDetail();
    transactionDetail.setId(transaction.getId());
    transactionDetail.setTransactionType(transaction.getTransactionType());
    transactionDetail.setAmount(transaction.getAmount());
    transactionDetail.setDescription(transaction.getDescription());
    transactionDetail.setTransactionDate(transaction.getTransactionDate());
    transactionDetail.setProductType(transaction.getAccountId() != null ? "ACCOUNT" : "CREDIT");
    transactionDetail.setProductNumber(""); // Puedes enriquecer esto si necesitas

    return transactionDetail;
  }

  // Método para mapear Customer a CustomerResponse (ajusta según tu implementación existente)
  private CustomerResponse mapToCustomerResponse(Customer customer) {
    CustomerResponse customerResponse = new CustomerResponse();
    customerResponse.setId(customer.getId());
    customerResponse.setDocumentType(customer.getDocumentType());
    customerResponse.setDocumentNumber(customer.getDocumentNumber());
    customerResponse.setFirstName(customer.getFirstName());
    customerResponse.setLastName(customer.getLastName());
    customerResponse.setEmail(customer.getEmail());
    customerResponse.setPhone(customer.getPhone());
    customerResponse.setCustomerType(CustomerTypeEnum.valueOf(customer.getCustomerType()));
    if (customer.getCreatedAt() != null) {
      customerResponse.setCreatedAt(customer.getCreatedAt().atOffset(ZoneOffset.UTC));
    }

    return customerResponse;
  }

}
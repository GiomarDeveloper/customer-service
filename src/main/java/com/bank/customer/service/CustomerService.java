package com.bank.customer.service;

import com.bank.customer.model.ConsolidatedSummary;
import com.bank.customer.model.CustomerMonthlySummary;
import com.bank.customer.model.CustomerRequest;
import com.bank.customer.model.CustomerResponse;
import com.bank.customer.model.ProductReportRequest;
import com.bank.customer.model.ProductReportResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
  Flux<CustomerResponse> findAll();
  Mono<CustomerResponse> findById(String id);
  Mono<CustomerResponse> findByDocumentNumber(String documentNumber);
  Mono<CustomerResponse> create(CustomerRequest request);
  Mono<CustomerResponse> update(String id, CustomerRequest request);
  Mono<Void> delete(String id);
  Mono<CustomerMonthlySummary> generateMonthlySummary(String customerId);
  Mono<ConsolidatedSummary> getConsolidatedSummary(String customerId);
  Mono<ProductReportResponse> generateProductReport(ProductReportRequest request);
}
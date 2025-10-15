package com.bank.customer.controller;

import com.bank.customer.dto.CustomerRequest;
import com.bank.customer.dto.CustomerResponse;
import com.bank.customer.exception.ErrorResponse;
import com.bank.customer.mapper.CustomerMapper;
import com.bank.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Operations related to bank customers")
public class CustomerController {

    private final CustomerService service;
    private final CustomerMapper mapper;

    @Operation(summary = "Get all customers", description = "Retrieve all registered customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public Flux<CustomerResponse> getAll() {
        return service.findAll()
                .map(mapper::toResponse);
    }

    @Operation(summary = "Get customer by ID", description = "Find a customer using its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public Mono<CustomerResponse> getById(@PathVariable String id) {
        return service.findById(id)
                .map(mapper::toResponse);
    }

    @Operation(summary = "Get customer by document", description = "Retrieve a customer by document number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/by-document/{documentNumber}")
    public Mono<CustomerResponse> getByDocument(@PathVariable String documentNumber) {
        return service.findByDocumentNumber(documentNumber).map((mapper::toResponse));
    }

    @Operation(summary = "Create customer", description = "Register a new customer in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return service.create(mapper.toEntity(request))
                .map(mapper::toResponse);
    }

    @Operation(summary = "Update customer", description = "Update an existing customer's data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public Mono<CustomerResponse> update(@PathVariable String id,
                                         @Valid @RequestBody CustomerRequest request) {
        return service.update(id, mapper.toEntity(request))
                .map(mapper::toResponse);
    }

    @Operation(summary = "Delete customer", description = "Delete a customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }
}
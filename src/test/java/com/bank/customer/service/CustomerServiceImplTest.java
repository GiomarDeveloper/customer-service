package com.bank.customer.service;

import com.bank.customer.model.Customer;
import com.bank.customer.repository.CustomerRepository;
import com.bank.customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

class CustomerServiceImplTest {

    private CustomerRepository repo;
    private CustomerServiceImpl customerService;

    /*
    @BeforeEach
    void setUp() {
        repo = Mockito.mock(CustomerRepository.class);
        customerService = new CustomerServiceImpl(repo);  // Inyecta manualmente el mock
    }
*/

    /*
    @Test
    void testFindByIdSuccess() {
        Customer customer = new Customer("1", "DNI", "12345678", "John", "Doe",
                "john@mail.com", "999999999", "PERSONAL", Instant.now());

        Mockito.when(repo.findByDocumentNumber("12345678")).thenReturn(Mono.just(customer));

        StepVerifier.create(customerService.findByDocumentNumber("12345678"))
                .expectNext(customer)
                .verifyComplete();
    }
     */
}
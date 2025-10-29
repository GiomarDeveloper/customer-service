package com.bank.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Clase principal de la aplicación Customer Service.
 * Este microservicio maneja las operaciones de gestión de clientes.
 *
 * @author Bank System
 * @version 1.0
 */
@SpringBootApplication
@EnableEurekaClient
public class CustomerServiceApplication {

  /**
   * Método principal para iniciar la aplicación Customer Service.
   *
   * @param args argumentos de línea de comandos
   */
  public static void main(String[] args) {
    SpringApplication.run(CustomerServiceApplication.class, args);
  }

}
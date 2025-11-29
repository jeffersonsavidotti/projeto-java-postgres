package com.example.projeto_postgres.controller;

import com.example.projeto_postgres.model.Customer;
import com.example.projeto_postgres.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para operações CRUD de Clientes
 */
@RestController
@RequestMapping("/customers")
@Tag(name = "Clientes", description = "API para gerenciamento de clientes")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * CREATE - Criar um novo cliente
     * POST /customers
     */
    @Operation(summary = "Criar um novo cliente", description = "Cria um novo cliente no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já existe")
    })
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        // Verifica se já existe um cliente com o mesmo email
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Já existe um cliente com este email");
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    /**
     * READ - Listar todos os clientes
     * GET /customers
     */
    @Operation(summary = "Listar todos os clientes", description = "Retorna uma lista com todos os clientes cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.ok(customers);
    }

    /**
     * READ - Buscar um cliente por ID
     * GET /customers/{id}
     */
    @Operation(summary = "Buscar cliente por ID", description = "Retorna um cliente específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(
            @Parameter(description = "ID do cliente", required = true) @PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return ResponseEntity.ok(customer);
    }

    /**
     * READ - Buscar um cliente por email
     * GET /customers/email/{email}
     */
    @Operation(summary = "Buscar cliente por email", description = "Retorna um cliente específico pelo seu email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(
            @Parameter(description = "Email do cliente", required = true) @PathVariable String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com este email"));
        return ResponseEntity.ok(customer);
    }

    /**
     * UPDATE - Atualizar um cliente existente
     * PUT /customers/{id}
     */
    @Operation(summary = "Atualizar cliente", description = "Atualiza um cliente existente pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @Parameter(description = "ID do cliente", required = true) @PathVariable Long id, 
            @Valid @RequestBody Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Verifica se o email está sendo alterado e se já existe outro cliente com esse email
        if (!customer.getEmail().equals(customerDetails.getEmail()) && 
            customerRepository.existsByEmail(customerDetails.getEmail())) {
            throw new RuntimeException("Já existe um cliente com este email");
        }

        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        customer.setAddress(customerDetails.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * DELETE - Deletar um cliente
     * DELETE /customers/{id}
     */
    @Operation(summary = "Deletar cliente", description = "Remove um cliente do sistema pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID do cliente", required = true) @PathVariable Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado");
        }
        
        customerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


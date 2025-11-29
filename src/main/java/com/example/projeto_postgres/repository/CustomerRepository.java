package com.example.projeto_postgres.repository;

import com.example.projeto_postgres.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    /**
     * Busca um cliente por email
     * O Spring Data JPA gera automaticamente a query SQL
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Verifica se existe um cliente com o email informado
     */
    boolean existsByEmail(String email);
}


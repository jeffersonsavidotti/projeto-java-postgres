package com.example.projeto_postgres.repository;

import com.example.projeto_postgres.model.Customer;
import com.example.projeto_postgres.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Busca todos os pedidos de um cliente
     */
    List<Order> findByCustomer(Customer customer);
    
    /**
     * Busca todos os pedidos de um cliente por ID
     */
    List<Order> findByCustomerId(Long customerId);
}


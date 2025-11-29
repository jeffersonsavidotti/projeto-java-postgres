package com.example.projeto_postgres.repository;

import com.example.projeto_postgres.model.Order;
import com.example.projeto_postgres.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    /**
     * Busca todos os itens de um pedido
     */
    List<OrderItem> findByOrder(Order order);
    
    /**
     * Busca todos os itens de um pedido por ID
     */
    List<OrderItem> findByOrderId(Long orderId);
}


package com.example.projeto_postgres.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Pedido - Representa a tabela "orders" no banco PostgreSQL
 * 
 * Um pedido pertence a um cliente e pode ter vários itens
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relacionamento ManyToOne com Cliente
     * Muitos pedidos pertencem a um cliente
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "O cliente é obrigatório")
    @JsonIgnoreProperties("orders") // Evita loop infinito na serialização JSON
    private Customer customer;

    /**
     * Relacionamento OneToMany com ItensPedido
     * Um pedido pode ter vários itens
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("order") // Evita loop infinito na serialização JSON
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Calcula o valor total do pedido baseado nos itens
     */
    public Integer getTotalAmount() {
        return items.stream()
                .mapToInt(item -> item.getQuantity() * item.getProduct().getPriceInCents())
                .sum();
    }

    /**
     * Enum para status do pedido
     */
    public enum OrderStatus {
        PENDING,    // Pendente
        CONFIRMED,  // Confirmado
        SHIPPED,    // Enviado
        DELIVERED,  // Entregue
        CANCELLED   // Cancelado
    }
}


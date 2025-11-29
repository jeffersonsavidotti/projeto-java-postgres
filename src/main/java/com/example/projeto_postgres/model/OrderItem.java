package com.example.projeto_postgres.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade ItemPedido - Representa a tabela "order_items" no banco PostgreSQL
 * 
 * Esta entidade representa o relacionamento muitos-para-muitos entre Pedido e Produto
 * com informações adicionais (quantidade)
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relacionamento ManyToOne com Pedido
     * Muitos itens pertencem a um pedido
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "O pedido é obrigatório")
    @JsonIgnoreProperties("items") // Evita loop infinito na serialização JSON
    private Order order;

    /**
     * Relacionamento ManyToOne com Produto
     * Muitos itens podem referenciar o mesmo produto
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "O produto é obrigatório")
    @JsonIgnoreProperties("orderItems") // Evita loop infinito na serialização JSON
    private Product product;

    @Positive(message = "A quantidade deve ser maior que zero")
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Calcula o subtotal do item (quantidade * preço do produto)
     */
    public Integer getSubtotal() {
        return quantity * product.getPriceInCents();
    }
}


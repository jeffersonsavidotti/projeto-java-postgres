package com.example.projeto_postgres.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Cliente - Representa a tabela "customers" no banco PostgreSQL
 * 
 * Um cliente pode ter vários pedidos (relacionamento OneToMany)
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do cliente não pode estar vazio")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "O email do cliente não pode estar vazio")
    @Email(message = "O email deve ser válido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    /**
     * Relacionamento OneToMany com Pedidos
     * Um cliente pode ter vários pedidos
     * 
     * mappedBy = "customer": Indica que o relacionamento é gerenciado pela entidade Order
     * cascade = CascadeType.ALL: Operações em Customer se propagam para Order
     * orphanRemoval = true: Remove pedidos órfãos quando o cliente é removido
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("customer") // Evita loop infinito na serialização JSON
    private List<Order> orders = new ArrayList<>();
}


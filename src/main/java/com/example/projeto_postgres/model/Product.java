// Declaração do pacote - organiza a classe no modelo de domínio
package com.example.projeto_postgres.model;

// Importa anotações JPA (Jakarta Persistence API) para mapeamento objeto-relacional
// @Entity: Marca a classe como uma entidade JPA
// @Table: Especifica o nome da tabela no banco de dados
// @Id: Marca o campo como chave primária
// @GeneratedValue: Define como a chave primária será gerada
// @Column: Define propriedades da coluna no banco de dados
import jakarta.persistence.*;

// Importa anotações de validação do Bean Validation
// @NotBlank: Valida que o campo não seja nulo, vazio ou apenas espaços
// @Positive: Valida que o número seja positivo (maior que zero)
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

// Importa anotações do Lombok para reduzir código boilerplate
// @Getter: Gera automaticamente métodos getters para todos os campos
// @Setter: Gera automaticamente métodos setters para todos os campos
// @AllArgsConstructor: Gera um construtor com todos os campos
// @NoArgsConstructor: Gera um construtor sem argumentos (necessário para JPA)
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Entidade - Representa a tabela "products" no banco PostgreSQL
 * 
 * Esta classe é mapeada para uma tabela no banco de dados usando JPA (Jakarta Persistence API).
 * O JPA é um framework que permite trabalhar com bancos de dados relacionais usando objetos Java,
 * sem precisar escrever SQL manualmente na maioria dos casos.
 * 
 * DIFERENÇA DO PROJETO H2:
 * - A estrutura da entidade é IDÊNTICA (mesmo código funciona em ambos!)
 * - A diferença está apenas na configuração do banco (application.properties)
 * - O JPA abstrai as diferenças entre bancos de dados
 * 
 * O Spring Data JPA usa esta entidade para:
 * - Criar automaticamente a tabela no PostgreSQL (se ddl-auto=update)
 * - Converter objetos Java em registros SQL
 * - Converter registros SQL em objetos Java
 * - Os dados são persistidos permanentemente no PostgreSQL
 */
@Entity // Indica ao JPA que esta classe é uma entidade (será mapeada para uma tabela)
@Table(name = "products") // Especifica o nome da tabela no banco de dados (opcional, usa o nome da classe se omitido)
@Getter // Lombok: Gera getters automaticamente (ex: getId(), getName(), getPriceInCents())
@Setter // Lombok: Gera setters automaticamente (ex: setId(), setName(), setPriceInCents())
@AllArgsConstructor // Lombok: Gera construtor com todos os campos (ex: new Product(1L, "Notebook", 250000))
@NoArgsConstructor // Lombok: Gera construtor sem argumentos (necessário para o JPA criar instâncias)
public class Product {

    /**
     * Campo ID - Chave primária da tabela
     * 
     * O @Id marca este campo como chave primária.
     * O @GeneratedValue com strategy IDENTITY significa que o banco de dados
     * gerará automaticamente o valor usando SERIAL (PostgreSQL) ou AUTO_INCREMENT (MySQL).
     * 
     * No PostgreSQL:
     * - A coluna será criada como SERIAL ou BIGSERIAL
     * - O banco gera automaticamente valores sequenciais (1, 2, 3, ...)
     * 
     * GenerationType.IDENTITY: O banco de dados gera o ID automaticamente
     * Funciona tanto no H2 quanto no PostgreSQL!
     */
    @Id // Marca este campo como chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define que o ID será gerado automaticamente pelo banco
    private Long id; // Tipo Long para suportar IDs grandes

    /**
     * Campo Name - Nome do produto
     * 
     * @NotBlank: Validação que garante que o campo não seja:
     * - null
     * - String vazia ("")
     * - String com apenas espaços ("   ")
     * 
     * @Column: Define propriedades da coluna no banco:
     * - nullable = false: A coluna não aceita valores NULL
     * - length = 100: Define o tamanho máximo da coluna VARCHAR(100)
     * 
     * No PostgreSQL:
     * - A coluna será criada como VARCHAR(100) NOT NULL
     * - O banco valida a constraint de NOT NULL
     * 
     * Quando uma requisição POST/PUT chega com dados inválidos, o Spring
     * automaticamente retorna um erro 400 (Bad Request) com a mensagem especificada.
     */
    @NotBlank(message = "O nome do produto não pode estar vazio") // Validação: campo obrigatório e não pode ser vazio
    @Column(nullable = false, length = 100) // Define que a coluna é obrigatória e tem no máximo 100 caracteres
    private String name; // Nome do produto

    /**
     * Campo PriceInCents - Preço do produto em centavos
     * 
     * Armazenamos o preço em centavos (Integer) ao invés de usar BigDecimal
     * para evitar problemas de arredondamento com números decimais.
     * Exemplo: R$ 2.500,00 = 250000 centavos
     * 
     * @Positive: Valida que o valor seja maior que zero
     * @Column(nullable = false): Garante que o preço seja obrigatório
     * 
     * No PostgreSQL:
     * - A coluna será criada como INTEGER NOT NULL
     * - O banco valida a constraint de NOT NULL
     * 
     * O Spring Validation executa essas validações automaticamente quando
     * o método do controller recebe um objeto com @Valid.
     */
    @Positive(message = "O preço deve ser maior que zero") // Validação: número deve ser positivo (> 0)
    @Column(nullable = false) // Define que a coluna é obrigatória (não aceita NULL)
    private Integer priceInCents; // Preço em centavos para evitar problemas de arredondamento

    /**
     * Relacionamento OneToMany com ItensPedido
     * Um produto pode estar em vários itens de pedido
     * 
     * mappedBy = "product": Indica que o relacionamento é gerenciado pela entidade OrderItem
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("product") // Evita loop infinito na serialização JSON
    private List<OrderItem> orderItems = new ArrayList<>();
}


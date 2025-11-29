// Declaração do pacote - organiza a interface no pacote de repositórios
package com.example.projeto_postgres.repository;

// Importa a entidade Product que será gerenciada por este repositório
import com.example.projeto_postgres.model.Product;

// Importa JpaRepository do Spring Data JPA
// JpaRepository é uma interface que fornece métodos prontos para operações CRUD
// sem precisar implementar SQL manualmente
import org.springframework.data.jpa.repository.JpaRepository;

// Importa a anotação @Repository
// Marca esta interface como um componente Spring do tipo Repository
// O Spring automaticamente cria uma implementação desta interface em tempo de execução
import org.springframework.stereotype.Repository;

/**
 * Interface de Repositório - Camada de acesso a dados com PostgreSQL
 * 
 * O Spring Data JPA é uma abstração sobre o JPA que simplifica drasticamente
 * o acesso a dados. Ao estender JpaRepository, você ganha automaticamente:
 * 
 * MÉTODOS HERDADOS DE JpaRepository:
 * - save(entity): Salva ou atualiza uma entidade no PostgreSQL
 * - findById(id): Busca uma entidade pelo ID (retorna Optional)
 * - findAll(): Retorna todas as entidades da tabela
 * - deleteById(id): Deleta uma entidade pelo ID
 * - existsById(id): Verifica se uma entidade existe pelo ID
 * - count(): Conta o total de entidades
 * - E muitos outros métodos úteis!
 * 
 * COMO FUNCIONA:
 * 1. O Spring escaneia esta interface durante a inicialização
 * 2. Cria automaticamente uma implementação concreta em tempo de execução
 * 3. Esta implementação usa o EntityManager do JPA para executar as operações
 * 4. O JPA traduz as operações em SQL específico do PostgreSQL
 * 5. Você pode injetar este repositório em qualquer classe usando @Autowired
 * 
 * PARÂMETROS DO JpaRepository:
 * - Product: A entidade que este repositório gerencia
 * - Long: O tipo da chave primária da entidade (id)
 * 
 * DIFERENÇA DO PROJETO H2:
 * - O código é IDÊNTICO! O Spring Data JPA abstrai as diferenças entre bancos
 * - As queries SQL geradas são específicas do PostgreSQL
 * - Os dados são persistidos permanentemente no PostgreSQL
 * 
 * VANTAGENS:
 * - Zero código SQL necessário para operações básicas
 * - Type-safe: erros são detectados em tempo de compilação
 * - Fácil de testar: pode criar mocks facilmente
 * - Suporta paginação, ordenação e queries customizadas
 * - Funciona com qualquer banco de dados (H2, PostgreSQL, MySQL, Oracle, etc.)
 */
@Repository // Marca como componente Spring do tipo Repository (opcional, mas recomendado para clareza)
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Esta interface está vazia, mas herda todos os métodos do JpaRepository!
    // 
    // Você pode adicionar métodos customizados aqui seguindo convenções de nomenclatura:
    // - findByNome(String nome): Spring cria a query automaticamente
    // - findByPriceInCentsGreaterThan(Integer price): Busca produtos com preço maior que X
    // - findByNomeContaining(String nome): Busca produtos cujo nome contém a string
    // 
    // Exemplo de método customizado (descomente para usar):
    // List<Product> findByNameContainingIgnoreCase(String name);
    //
    // O Spring Data JPA gera automaticamente a query SQL:
    // SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?1)
}


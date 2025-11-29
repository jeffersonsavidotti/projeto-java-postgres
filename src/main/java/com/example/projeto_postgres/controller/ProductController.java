// Declaração do pacote - organiza a classe no pacote de controllers
package com.example.projeto_postgres.controller;

// Importa a entidade Product que será usada nas requisições/respostas
import com.example.projeto_postgres.model.Product;

// Importa o repositório para acessar os dados do banco PostgreSQL
import com.example.projeto_postgres.repository.ProductRepository;

// Importa @Valid para habilitar validações do Bean Validation
// Quando um objeto tem @Valid, o Spring valida automaticamente todas as anotações
// de validação (@NotBlank, @Positive, etc.) antes de executar o método
import jakarta.validation.Valid;

// Importa @Autowired para injeção de dependência
// O Spring automaticamente injeta uma instância do ProductRepository aqui
import org.springframework.beans.factory.annotation.Autowired;

// Importa HttpStatus para códigos HTTP padronizados (200, 201, 404, etc.)
import org.springframework.http.HttpStatus;

// Importa ResponseEntity para construir respostas HTTP com status e corpo
// Permite controlar o código de status, headers e corpo da resposta
import org.springframework.http.ResponseEntity;

// Importa anotações do Spring MVC para criar endpoints REST
// @RestController: Combina @Controller + @ResponseBody (retorna JSON automaticamente)
// @RequestMapping: Define o caminho base para todos os endpoints desta classe
// @PostMapping: Mapeia requisições HTTP POST
// @GetMapping: Mapeia requisições HTTP GET
// @PutMapping: Mapeia requisições HTTP PUT
// @DeleteMapping: Mapeia requisições HTTP DELETE
// @RequestBody: Converte o JSON do corpo da requisição em um objeto Java
// @PathVariable: Extrai variáveis da URL (ex: /products/{id})
import org.springframework.web.bind.annotation.*;

// Importa List para retornar coleções de produtos
import java.util.List;

// Importa anotações do Swagger/OpenAPI para documentação
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST - Camada de apresentação/API com PostgreSQL
 * 
 * O Controller é responsável por:
 * - Receber requisições HTTP do cliente
 * - Validar os dados recebidos
 * - Chamar a camada de serviço/repositório
 * - Retornar respostas HTTP apropriadas
 * 
 * DIFERENÇA DO PROJETO H2:
 * - O código do controller é IDÊNTICO!
 * - A diferença está apenas na configuração do banco
 * - Os dados são persistidos no PostgreSQL (permanentes)
 * - Adequado para produção
 * 
 * @RestController é uma anotação especial que combina:
 * - @Controller: Marca a classe como um controller Spring MVC
 * - @ResponseBody: Faz com que os métodos retornem JSON automaticamente
 *   (sem precisar de @ResponseBody em cada método)
 * 
 * O Spring Boot automaticamente:
 * - Converte objetos Java em JSON (usando Jackson)
 * - Converte JSON em objetos Java
 * - Trata erros e exceções
 * - Gerencia o ciclo de vida das requisições
 * - Usa o pool de conexões (HikariCP) para acessar o PostgreSQL
 */
@RestController // Marca como controller REST (retorna JSON automaticamente)
@RequestMapping("/products") // Define o caminho base: todos os endpoints começam com /products
@Tag(name = "Produtos", description = "API para gerenciamento de produtos")
public class ProductController {

    /**
     * Injeção de Dependência do Repositório
     * 
     * @Autowired: O Spring automaticamente injeta uma instância do ProductRepository
     * 
     * COMO FUNCIONA A INJEÇÃO DE DEPENDÊNCIA:
     * 1. Durante a inicialização, o Spring cria uma instância do ProductRepository
     * 2. Quando cria o ProductController, vê o @Autowired
     * 3. Injeta automaticamente a instância do repositório aqui
     * 
     * COM POSTGRESQL:
     * - O repositório usa conexões do pool (HikariCP)
     * - As operações são executadas no PostgreSQL
     * - Os dados são persistidos permanentemente
     * 
     * VANTAGENS:
     * - Facilita testes (pode injetar mocks)
     * - Reduz acoplamento entre classes
     * - O Spring gerencia o ciclo de vida dos objetos
     * 
     * ALTERNATIVA MODERNA: Injeção via construtor (mais recomendado)
     * public ProductController(ProductRepository productRepository) {
     *     this.productRepository = productRepository;
     * }
     */
    @Autowired // Injeção de dependência: Spring injeta automaticamente o ProductRepository
    private ProductRepository productRepository; // Repositório para acessar dados do PostgreSQL

    /**
     * CREATE - Criar um novo produto
     * 
     * Endpoint: POST http://localhost:8080/products
     * 
     * @PostMapping: Mapeia requisições HTTP POST para este método
     * Sem caminho adicional, então responde a POST /products
     * 
     * @Valid: Habilita validação automática do objeto Product
     * Se o objeto for inválido (ex: name vazio, price negativo),
     * o Spring retorna automaticamente erro 400 (Bad Request)
     * 
     * @RequestBody: Converte o JSON do corpo da requisição em um objeto Product
     * Exemplo de JSON esperado:
     * {
     *   "name": "Notebook",
     *   "priceInCents": 250000
     * }
     * 
     * COM POSTGRESQL:
     * - O produto é salvo permanentemente no banco
     * - O ID é gerado pelo PostgreSQL usando SERIAL
     * - A transação é commitada automaticamente
     * 
     * ResponseEntity: Permite controlar o código HTTP e o corpo da resposta
     * - HttpStatus.CREATED (201): Indica que o recurso foi criado com sucesso
     * - body(savedProduct): Retorna o produto criado (com ID gerado) em JSON
     */
    @Operation(summary = "Criar um novo produto", description = "Cria um novo produto no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping // Mapeia requisições HTTP POST para /products
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        // Salva o produto no banco PostgreSQL
        // O método save() do JPA:
        // - Se o ID for null: cria um novo registro (INSERT INTO products ...)
        // - Se o ID existir: atualiza o registro existente (UPDATE products ...)
        // - O PostgreSQL gera o ID automaticamente usando SERIAL
        Product savedProduct = productRepository.save(product);
        
        // Retorna resposta HTTP 201 (Created) com o produto criado no corpo
        // O Spring automaticamente converte o objeto Product em JSON
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    /**
     * READ - Listar todos os produtos
     * 
     * Endpoint: GET http://localhost:8080/products
     * 
     * @GetMapping: Mapeia requisições HTTP GET para este método
     * 
     * COM POSTGRESQL:
     * - Executa: SELECT * FROM products
     * - Retorna todos os produtos salvos permanentemente no banco
     * - Os dados persistem mesmo após reiniciar a aplicação
     * 
     * ResponseEntity.ok(): Método helper que retorna HTTP 200 (OK)
     * Equivale a: ResponseEntity.status(HttpStatus.OK).body(products)
     * 
     * O Spring automaticamente serializa a List<Product> em JSON:
     * [
     *   {"id": 1, "name": "Notebook", "priceInCents": 250000},
     *   {"id": 2, "name": "Mouse", "priceInCents": 5000}
     * ]
     */
    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista com todos os produtos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    @GetMapping // Mapeia requisições HTTP GET para /products
    public ResponseEntity<List<Product>> getAllProducts() {
        // Busca todos os produtos no banco PostgreSQL
        // findAll() executa: SELECT * FROM products
        // Retorna uma List com todos os registros da tabela
        List<Product> products = productRepository.findAll();
        
        // Retorna HTTP 200 (OK) com a lista de produtos em JSON
        return ResponseEntity.ok(products);
    }

    /**
     * READ - Buscar um produto específico por ID
     * 
     * Endpoint: GET http://localhost:8080/products/1
     * 
     * @GetMapping("/{id}"): O {id} é uma variável de caminho (path variable)
     * Exemplo: GET /products/1 → id = 1
     * 
     * @PathVariable: Extrai o valor {id} da URL e injeta no parâmetro Long id
     * 
     * COM POSTGRESQL:
     * - Executa: SELECT * FROM products WHERE id = ?
     * - Usa prepared statements (proteção contra SQL injection)
     * 
     * findById(): Retorna um Optional<Product>
     * - Se encontrar: Optional contém o Product
     * - Se não encontrar: Optional vazio
     * 
     * orElseThrow(): Se o Optional estiver vazio, lança uma exceção
     * O GlobalExceptionHandler captura essa exceção e retorna HTTP 404
     */
    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}") // Mapeia GET /products/{id} - {id} é uma variável de caminho
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID do produto", required = true) @PathVariable Long id) {
        // Busca o produto pelo ID no PostgreSQL
        // findById() executa: SELECT * FROM products WHERE id = ?
        // Retorna Optional<Product>:
        // - Se encontrar: Optional<Product> com o produto
        // - Se não encontrar: Optional.empty()
        Product product = productRepository.findById(id)
                // Se não encontrar, lança RuntimeException
                // O GlobalExceptionHandler captura e retorna HTTP 404
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
        // Retorna HTTP 200 (OK) com o produto encontrado
        return ResponseEntity.ok(product);
    }

    /**
     * UPDATE - Atualizar um produto existente
     * 
     * Endpoint: PUT http://localhost:8080/products/1
     * 
     * @PutMapping("/{id}"): Mapeia requisições HTTP PUT
     * PUT é usado para atualizar recursos existentes
     * 
     * COM POSTGRESQL:
     * - Busca: SELECT * FROM products WHERE id = ?
     * - Atualiza: UPDATE products SET name = ?, price_in_cents = ? WHERE id = ?
     * - Os dados são atualizados permanentemente no banco
     * 
     * Fluxo de atualização:
     * 1. Busca o produto existente no banco
     * 2. Atualiza os campos com os novos valores
     * 3. Salva novamente (o JPA detecta que é update porque o ID existe)
     * 
     * @Valid: Valida os dados do produtoDetails antes de atualizar
     * 
     * IMPORTANTE: Esta é uma atualização parcial (PATCH seria mais semântico)
     * Para atualização completa, você deveria validar todos os campos obrigatórios
     */
    @Operation(summary = "Atualizar produto", description = "Atualiza um produto existente pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}") // Mapeia requisições HTTP PUT para /products/{id}
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "ID do produto", required = true) @PathVariable Long id, 
            @Valid @RequestBody Product productDetails) {
        // Busca o produto existente no banco PostgreSQL
        // Se não encontrar, lança exceção (tratada pelo GlobalExceptionHandler)
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Atualiza os campos do produto existente com os novos valores
        // Não atualizamos o ID (chave primária não deve ser alterada)
        product.setName(productDetails.getName()); // Atualiza o nome
        product.setPriceInCents(productDetails.getPriceInCents()); // Atualiza o preço

        // Salva o produto atualizado no PostgreSQL
        // O JPA detecta que o ID já existe e faz UPDATE ao invés de INSERT
        // Executa: UPDATE products SET name = ?, price_in_cents = ? WHERE id = ?
        Product updatedProduct = productRepository.save(product);
        
        // Retorna HTTP 200 (OK) com o produto atualizado
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * DELETE - Deletar um produto
     * 
     * Endpoint: DELETE http://localhost:8080/products/1
     * 
     * @DeleteMapping("/{id}"): Mapeia requisições HTTP DELETE
     * 
     * COM POSTGRESQL:
     * - Verifica: SELECT COUNT(*) FROM products WHERE id = ?
     * - Deleta: DELETE FROM products WHERE id = ?
     * - O registro é removido permanentemente do banco
     * 
     * ResponseEntity<Void>: Retorna resposta sem corpo (apenas status HTTP)
     * 
     * noContent(): Retorna HTTP 204 (No Content) - padrão para DELETE bem-sucedido
     * 
     * existsById(): Verifica se o produto existe antes de deletar
     * Evita exceções desnecessárias se o ID não existir
     */
    @Operation(summary = "Deletar produto", description = "Remove um produto do sistema pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}") // Mapeia requisições HTTP DELETE para /products/{id}
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID do produto", required = true) @PathVariable Long id) {
        // Verifica se o produto existe no PostgreSQL antes de tentar deletar
        // Executa: SELECT COUNT(*) FROM products WHERE id = ?
        // Se não existir, lança exceção (retorna HTTP 404)
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado");
        }
        
        // Deleta o produto do banco PostgreSQL
        // deleteById() executa: DELETE FROM products WHERE id = ?
        // Remove o registro permanentemente da tabela
        productRepository.deleteById(id);
        
        // Retorna HTTP 204 (No Content) - resposta sem corpo
        // Indica que a operação foi bem-sucedida, mas não há conteúdo para retornar
        return ResponseEntity.noContent().build();
    }
}


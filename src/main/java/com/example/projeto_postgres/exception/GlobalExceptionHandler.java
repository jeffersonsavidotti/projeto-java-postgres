// Declaração do pacote - organiza a classe no pacote de tratamento de exceções
package com.example.projeto_postgres.exception;

// Importa HttpStatus para códigos HTTP padronizados
import org.springframework.http.HttpStatus;

// Importa ResponseEntity para construir respostas HTTP
import org.springframework.http.ResponseEntity;

// Importa MethodArgumentNotValidException
// Esta exceção é lançada quando validações do Bean Validation falham
// (ex: @NotBlank, @Positive, etc.)
import org.springframework.web.bind.MethodArgumentNotValidException;

// Importa @ExceptionHandler para tratar exceções específicas
import org.springframework.web.bind.annotation.ExceptionHandler;

// Importa @RestControllerAdvice
// Esta anotação marca a classe como um handler global de exceções
// Todas as exceções lançadas nos controllers serão capturadas aqui
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Importa HashMap e Map para construir respostas JSON de erro
import java.util.HashMap;
import java.util.Map;

/**
 * Handler Global de Exceções
 * 
 * Esta classe centraliza o tratamento de exceções em toda a aplicação.
 * 
 * @RestControllerAdvice: Marca a classe como um handler global
 * - Escaneia todos os controllers automaticamente
 * - Captura exceções lançadas em qualquer controller
 * - Permite retornar respostas HTTP padronizadas para erros
 * 
 * DIFERENÇA DO PROJETO H2:
 * - O código é IDÊNTICO! Funciona da mesma forma em ambos os projetos
 * - As exceções podem vir de erros de conexão com PostgreSQL
 * - Pode tratar exceções específicas do banco (ex: SQLException)
 * 
 * VANTAGENS:
 * - Código de tratamento de erro centralizado
 * - Respostas de erro consistentes em toda a API
 * - Facilita manutenção e debugging
 * - Evita duplicação de código de tratamento de erro
 * 
 * FLUXO:
 * 1. Controller lança uma exceção
 * 2. Spring intercepta a exceção
 * 3. Procura um método @ExceptionHandler que trate esse tipo de exceção
 * 4. Executa o método e retorna a resposta HTTP apropriada
 */
@RestControllerAdvice // Marca como handler global de exceções para todos os controllers
public class GlobalExceptionHandler {

    /**
     * Trata exceções do tipo RuntimeException
     * 
     * Este método captura RuntimeExceptions lançadas nos controllers.
     * No nosso caso, usamos RuntimeException quando um produto não é encontrado.
     * 
     * @ExceptionHandler: Define que este método trata exceções do tipo especificado
     * Pode tratar a exceção especificada e suas subclasses
     * 
     * @param ex A exceção que foi lançada
     * @return ResponseEntity com o erro formatado em JSON
     * 
     * Exemplo de resposta JSON:
     * {
     *   "message": "Produto não encontrado",
     *   "status": "404"
     * }
     * 
     * COM POSTGRESQL:
     * - Também pode capturar erros de conexão com o banco
     * - Pode tratar exceções de constraint violations (ex: chave duplicada)
     */
    @ExceptionHandler(RuntimeException.class) // Trata exceções do tipo RuntimeException
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        // Cria um Map para construir a resposta JSON de erro
        Map<String, String> error = new HashMap<>();
        
        // Adiciona a mensagem da exceção ao JSON
        error.put("message", ex.getMessage());
        
        // Adiciona o código HTTP 404 (Not Found) ao JSON
        // value() retorna o código numérico (404)
        error.put("status", String.valueOf(HttpStatus.NOT_FOUND.value()));
        
        // Retorna resposta HTTP 404 com o JSON de erro no corpo
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata exceções de validação (Bean Validation)
     * 
     * Este método captura erros de validação quando dados inválidos são enviados.
     * Exemplo: enviar um produto com name vazio ou priceInCents negativo.
     * 
     * @ExceptionHandler: Trata MethodArgumentNotValidException
     * Esta exceção é lançada automaticamente pelo Spring quando:
     * - Um objeto tem @Valid mas os dados são inválidos
     * - As anotações de validação (@NotBlank, @Positive, etc.) falham
     * 
     * @param ex A exceção de validação contendo todos os erros de campo
     * @return ResponseEntity com todos os erros de validação em JSON
     * 
     * Exemplo de resposta JSON quando name está vazio e priceInCents é negativo:
     * {
     *   "name": "O nome do produto não pode estar vazio",
     *   "priceInCents": "O preço deve ser maior que zero",
     *   "status": "400"
     * }
     * 
     * COM POSTGRESQL:
     * - As validações acontecem ANTES de chegar no banco
     * - Evita queries desnecessárias ao PostgreSQL
     * - Melhora performance e segurança
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) // Trata erros de validação
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Cria um Map para armazenar todos os erros de validação
        Map<String, String> errors = new HashMap<>();
        
        // Itera sobre todos os erros de campo encontrados na validação
        // getBindingResult(): Obtém o resultado da validação
        // getFieldErrors(): Retorna uma lista com todos os campos que falharam na validação
        // forEach: Para cada erro de campo, adiciona ao Map
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            // Adiciona o nome do campo e a mensagem de erro ao Map
            // error.getField(): Nome do campo que falhou (ex: "name", "priceInCents")
            // error.getDefaultMessage(): Mensagem de erro definida na anotação (ex: "O nome do produto não pode estar vazio")
            errors.put(error.getField(), error.getDefaultMessage());
        });
        
        // Adiciona o código HTTP 400 (Bad Request) ao JSON
        // 400 indica que a requisição está malformada ou inválida
        errors.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        
        // Retorna resposta HTTP 400 com todos os erros de validação no corpo
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}


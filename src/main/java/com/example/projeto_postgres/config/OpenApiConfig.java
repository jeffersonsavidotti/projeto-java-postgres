package com.example.projeto_postgres.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Swagger/OpenAPI
 * 
 * Esta classe configura a documentação automática da API usando SpringDoc OpenAPI.
 * 
 * Após iniciar a aplicação, acesse:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API CRUD - Sistema de Produtos, Clientes e Pedidos")
                        .version("1.0.0")
                        .description("API REST completa para gerenciamento de produtos, clientes e pedidos com PostgreSQL")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Servidor de Produção")));
    }
}


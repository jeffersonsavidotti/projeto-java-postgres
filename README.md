# Sistema CRUD - Produtos, Clientes e Pedidos

Este projeto é uma implementação completa de um sistema CRUD (Create, Read, Update, Delete) usando Spring Boot e PostgreSQL, com gerenciamento de **Produtos**, **Clientes** e **Pedidos** com relacionamentos entre as entidades.

## 📋 Pré-requisitos

- Java 24 ou superior
- Maven 3.6+
- PostgreSQL instalado e rodando
- Banco de dados criado (veja instruções abaixo)

## 🗄️ Configuração do Banco de Dados PostgreSQL

### 1. Instalar PostgreSQL

Se ainda não tiver o PostgreSQL instalado, baixe em: https://www.postgresql.org/download/

### 2. Criar o Banco de Dados

Abra o terminal/command prompt e execute:

```sql
-- Conecte-se ao PostgreSQL
psql -U postgres

-- Crie o banco de dados
CREATE DATABASE crud_db;

-- Verifique se foi criado
\l
```

### 3. Configurar as Credenciais

Edite o arquivo `src/main/resources/application.properties` e ajuste as seguintes propriedades conforme sua configuração:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/crud_db
spring.datasource.username=postgres
spring.datasource.password=sua_senha_aqui
```

## 🚀 Como Executar

### 1. Clone ou navegue até a pasta do projeto

```bash
cd projeto-postgres
```

### 2. Execute a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 📚 Documentação da API (Swagger)

A documentação interativa da API está disponível através do Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

O Swagger UI permite:
- Visualizar todos os endpoints disponíveis
- Testar os endpoints diretamente na interface
- Ver exemplos de requisições e respostas
- Entender os schemas de dados

## 📡 Endpoints da API

### 🛍️ Produtos

#### Criar Produto
```http
POST http://localhost:8080/products
Content-Type: application/json

{
  "name": "Notebook",
  "priceInCents": 250000
}
```

#### Listar Todos os Produtos
```http
GET http://localhost:8080/products
```

#### Buscar Produto por ID
```http
GET http://localhost:8080/products/1
```

#### Atualizar Produto
```http
PUT http://localhost:8080/products/1
Content-Type: application/json

{
  "name": "Notebook Atualizado",
  "priceInCents": 300000
}
```

#### Deletar Produto
```http
DELETE http://localhost:8080/products/1
```

### 👥 Clientes

#### Criar Cliente
```http
POST http://localhost:8080/customers
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "phone": "11999999999",
  "address": "Rua Exemplo, 123"
}
```

#### Listar Todos os Clientes
```http
GET http://localhost:8080/customers
```

#### Buscar Cliente por ID
```http
GET http://localhost:8080/customers/1
```

#### Buscar Cliente por Email
```http
GET http://localhost:8080/customers/email/joao@email.com
```

#### Atualizar Cliente
```http
PUT http://localhost:8080/customers/1
Content-Type: application/json

{
  "name": "João Silva Atualizado",
  "email": "joao.novo@email.com",
  "phone": "11988888888",
  "address": "Rua Nova, 456"
}
```

#### Deletar Cliente
```http
DELETE http://localhost:8080/customers/1
```

### 📦 Pedidos

#### Criar Pedido
```http
POST http://localhost:8080/orders
Content-Type: application/json

{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

#### Listar Todos os Pedidos
```http
GET http://localhost:8080/orders
```

#### Buscar Pedido por ID
```http
GET http://localhost:8080/orders/1
```

#### Buscar Pedidos de um Cliente
```http
GET http://localhost:8080/orders/customer/1
```

#### Atualizar Status do Pedido
```http
PUT http://localhost:8080/orders/1/status
Content-Type: application/json

{
  "status": "CONFIRMED"
}
```

**Status disponíveis:**
- `PENDING` - Pendente
- `CONFIRMED` - Confirmado
- `SHIPPED` - Enviado
- `DELIVERED` - Entregue
- `CANCELLED` - Cancelado

#### Deletar Pedido
```http
DELETE http://localhost:8080/orders/1
```

## 🏗️ Modelo de Dados

### Relacionamentos

```
Cliente (1) ────< (N) Pedido
                        │
                        │ (1)
                        │
                        ▼
                    ItemPedido (N)
                        │
                        │ (N)
                        │
                        ▼
                    Produto (1)
```

- **Cliente** → **Pedido**: Um cliente pode ter vários pedidos (OneToMany)
- **Pedido** → **ItemPedido**: Um pedido pode ter vários itens (OneToMany)
- **Produto** → **ItemPedido**: Um produto pode estar em vários itens de pedido (OneToMany)
- **ItemPedido** → **Pedido**: Muitos itens pertencem a um pedido (ManyToOne)
- **ItemPedido** → **Produto**: Muitos itens referenciam um produto (ManyToOne)

### Entidades

#### Cliente (Customer)
- `id` - Identificador único
- `name` - Nome do cliente (obrigatório)
- `email` - Email do cliente (obrigatório, único)
- `phone` - Telefone (opcional)
- `address` - Endereço (opcional)
- `orders` - Lista de pedidos do cliente

#### Pedido (Order)
- `id` - Identificador único
- `customer` - Cliente que fez o pedido (obrigatório)
- `items` - Lista de itens do pedido
- `orderDate` - Data do pedido (gerada automaticamente)
- `status` - Status do pedido (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
- `getTotalAmount()` - Calcula o valor total do pedido

#### ItemPedido (OrderItem)
- `id` - Identificador único
- `order` - Pedido ao qual o item pertence (obrigatório)
- `product` - Produto do item (obrigatório)
- `quantity` - Quantidade (obrigatório, maior que zero)
- `getSubtotal()` - Calcula o subtotal do item

#### Produto (Product)
- `id` - Identificador único
- `name` - Nome do produto (obrigatório)
- `priceInCents` - Preço em centavos (obrigatório, maior que zero)
- `orderItems` - Lista de itens de pedido que contêm este produto

## 🛠️ Tecnologias Utilizadas

- **Spring Boot 3.5.7** - Framework Java
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados relacional
- **Lombok** - Redução de boilerplate
- **Bean Validation** - Validação de dados
- **SpringDoc OpenAPI** - Documentação da API (Swagger)

## 📁 Estrutura do Projeto

```
projeto-postgres/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/projeto_postgres/
│   │   │       ├── config/
│   │   │       │   └── OpenApiConfig.java          # Configuração do Swagger
│   │   │       ├── controller/
│   │   │       │   ├── ProductController.java     # Endpoints de Produtos
│   │   │       │   ├── CustomerController.java    # Endpoints de Clientes
│   │   │       │   └── OrderController.java        # Endpoints de Pedidos
│   │   │       ├── exception/
│   │   │       │   └── GlobalExceptionHandler.java # Tratamento de exceções
│   │   │       ├── model/
│   │   │       │   ├── Product.java               # Entidade Produto
│   │   │       │   ├── Customer.java              # Entidade Cliente
│   │   │       │   ├── Order.java                  # Entidade Pedido
│   │   │       │   └── OrderItem.java             # Entidade ItemPedido
│   │   │       ├── repository/
│   │   │       │   ├── ProductRepository.java      # Repositório de Produtos
│   │   │       │   ├── CustomerRepository.java    # Repositório de Clientes
│   │   │       │   ├── OrderRepository.java        # Repositório de Pedidos
│   │   │       │   └── OrderItemRepository.java    # Repositório de ItensPedido
│   │   │       └── ProjetoPostgresApplication.java # Classe principal
│   │   └── resources/
│   │       └── application.properties              # Configurações
│   └── test/
└── pom.xml
```

## 🔍 Verificando os Dados no PostgreSQL

Para verificar os dados inseridos, você pode usar o `psql`:

```bash
psql -U postgres -d crud_db

-- Listar todas as tabelas
\dt

-- Listar todos os produtos
SELECT * FROM products;

-- Listar todos os clientes
SELECT * FROM customers;

-- Listar todos os pedidos
SELECT * FROM orders;

-- Listar todos os itens de pedido
SELECT * FROM order_items;

-- Consulta com relacionamentos (pedidos com cliente e itens)
SELECT 
    o.id as pedido_id,
    c.name as cliente_nome,
    o.order_date,
    o.status,
    p.name as produto_nome,
    oi.quantity,
    p.price_in_cents
FROM orders o
JOIN customers c ON o.customer_id = c.id
JOIN order_items oi ON oi.order_id = o.id
JOIN products p ON oi.product_id = p.id;
```

Ou use uma ferramenta gráfica como:
- **pgAdmin** (https://www.pgadmin.org/)
- **DBeaver** (https://dbeaver.io/)
- **DataGrip** (JetBrains)

## ⚙️ Configurações Adicionais

### Connection Pool

O projeto já está configurado com HikariCP (pool de conexões padrão do Spring Boot):

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### Logs SQL

Os logs SQL estão habilitados para facilitar o debug. Para desabilitar, altere:

```properties
spring.jpa.show-sql=false
```

### Swagger/OpenAPI

O Swagger está configurado e pode ser acessado em:
- Interface: http://localhost:8080/swagger-ui.html
- JSON: http://localhost:8080/v3/api-docs

## 🎯 Exemplo de Fluxo Completo

### 1. Criar um Produto
```http
POST http://localhost:8080/products
{
  "name": "Notebook Dell",
  "priceInCents": 350000
}
```

### 2. Criar um Cliente
```http
POST http://localhost:8080/customers
{
  "name": "Maria Santos",
  "email": "maria@email.com",
  "phone": "11977777777",
  "address": "Av. Principal, 789"
}
```

### 3. Criar um Pedido
```http
POST http://localhost:8080/orders
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 1
    }
  ]
}
```

### 4. Atualizar Status do Pedido
```http
PUT http://localhost:8080/orders/1/status
{
  "status": "CONFIRMED"
}
```

### 5. Buscar Pedidos do Cliente
```http
GET http://localhost:8080/orders/customer/1
```

## 🐛 Troubleshooting

### Erro de Conexão

Se receber erro de conexão, verifique:
1. PostgreSQL está rodando
2. Credenciais estão corretas no `application.properties`
3. Banco de dados `crud_db` foi criado
4. Porta 5432 está acessível

### Erro de Permissão

Se houver erro de permissão, certifique-se de que o usuário `postgres` tem permissões para criar tabelas no banco.

### Erro de Relacionamento

Se houver erro ao deletar um cliente que tem pedidos:
- O sistema está configurado com `orphanRemoval = true`, então os pedidos serão deletados automaticamente quando o cliente for removido
- Para evitar isso, delete os pedidos primeiro ou ajuste a estratégia de cascade

## 📝 Notas

- As tabelas serão criadas automaticamente na primeira execução (devido ao `spring.jpa.hibernate.ddl-auto=update`)
- Os dados persistem no PostgreSQL (diferente do H2 que é em memória)
- Para produção, considere usar `spring.jpa.hibernate.ddl-auto=validate` ou `none` e gerenciar o schema com migrations (Flyway ou Liquibase)
- O email do cliente deve ser único no sistema
- O preço dos produtos é armazenado em centavos para evitar problemas de arredondamento
- Os relacionamentos são configurados com lazy loading para melhor performance

## 📄 Licença

Este projeto é um exemplo educacional e pode ser usado livremente para fins de aprendizado.

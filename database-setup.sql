-- Script SQL para criar o banco de dados e configurar o PostgreSQL
-- Execute este script como usuário postgres ou com permissões de superusuário

-- Criar o banco de dados
CREATE DATABASE crud_db;

-- Conectar ao banco de dados criado
\c crud_db;

-- (Opcional) Criar um usuário específico para a aplicação
-- CREATE USER app_user WITH PASSWORD 'sua_senha_segura';
-- GRANT ALL PRIVILEGES ON DATABASE crud_db TO app_user;

-- A tabela 'products' será criada automaticamente pelo Hibernate
-- na primeira execução da aplicação devido ao 'spring.jpa.hibernate.ddl-auto=update'

-- Para verificar se tudo está funcionando após a primeira execução:
-- SELECT * FROM products;


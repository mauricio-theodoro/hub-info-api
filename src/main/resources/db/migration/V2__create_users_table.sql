-- V2__create_users_table.sql
-- Cria a tabela de usuários base do HUB Info.
-- Observação: decisões como tamanho de campo e índices foram feitas para uso empresarial e auditoria.

CREATE TABLE users (
    id            CHAR(36)      NOT NULL,
    first_name    VARCHAR(80)   NOT NULL,
    last_name     VARCHAR(120)  NOT NULL,
    email         VARCHAR(320)  NOT NULL,
    password_hash VARCHAR(255)  NOT NULL,
    birth_date    DATE          NOT NULL,
    role          VARCHAR(30)   NOT NULL,

    created_at    TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    TIMESTAMP(6)  NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;

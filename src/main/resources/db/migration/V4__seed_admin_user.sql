-- V4__seed_admin_user.sql
-- Seed do usuário ADMIN para ambiente DEV.
-- Regras:
-- 1) Não sobrescreve se já existir.
-- 2) Depende do hash BCrypt fornecido por variável de ambiente.

-- MySQL não permite ler env var diretamente no SQL.
-- Então a estratégia é: o app injeta placeholders via Flyway placeholders.

INSERT INTO users (id, first_name, last_name, email, password_hash, birth_date, roles, created_at)
SELECT
  UUID(),
  '${hubinfo_bootstrap_admin_first_name}',
  '${hubinfo_bootstrap_admin_last_name}',
  '${hubinfo_bootstrap_admin_email}',
  '${hubinfo_bootstrap_admin_password_hash}',
  '${hubinfo_bootstrap_admin_birth_date}',
  'ADMIN',
  NOW(6)
WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE email = '${hubinfo_bootstrap_admin_email}'
);

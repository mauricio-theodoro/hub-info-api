-- V3__rename_role_to_roles.sql
-- Padroniza a coluna de perfis para "roles" (CSV: USER,ADMIN).

ALTER TABLE users
  ADD COLUMN roles VARCHAR(200) NOT NULL AFTER birth_date;

UPDATE users
  SET roles = role;

ALTER TABLE users
  DROP COLUMN role;

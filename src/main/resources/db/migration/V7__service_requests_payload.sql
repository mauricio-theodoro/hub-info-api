-- Ajuste/Criação da tabela service_requests para suportar múltiplos serviços (CND/DT-e/etc.)
CREATE TABLE IF NOT EXISTS service_requests (
  id CHAR(36) NOT NULL PRIMARY KEY,
  actor_user_id CHAR(36) NOT NULL,
  actor_email VARCHAR(255) NOT NULL,
  service_type VARCHAR(60) NOT NULL,
  status VARCHAR(40) NOT NULL,
  payload_json LONGTEXT NOT NULL,
  requested_at TIMESTAMP(6) NOT NULL,
  INDEX idx_service_requests_actor_email (actor_email),
  INDEX idx_service_requests_service_type (service_type),
  INDEX idx_service_requests_requested_at (requested_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

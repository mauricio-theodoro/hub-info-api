CREATE TABLE IF NOT EXISTS service_requests (
  id                    CHAR(36)      NOT NULL,
  service_type           VARCHAR(40)   NOT NULL,  -- ex: CND
  status                 VARCHAR(30)   NOT NULL,  -- PENDING, SUCCESS, FAILURE

  cnpj                   CHAR(14)      NOT NULL,

  requested_by_user_id   CHAR(36)      NULL,
  requested_by_email     VARCHAR(255)  NULL,

  requested_at           TIMESTAMP(6)  NOT NULL,
  completed_at           TIMESTAMP(6)  NULL,

  result_code            VARCHAR(60)   NULL,      -- ex: CAPTCHA_REQUIRED, ISSUED, POSITIVE
  result_message         VARCHAR(255)  NULL,      -- mensagem curta para UI
  result_payload_json    JSON          NULL,      -- detalhes adicionais (se necessário)

  PRIMARY KEY (id),
  INDEX idx_sr_requested_at (requested_at),
  INDEX idx_sr_service_type (service_type),
  INDEX idx_sr_cnpj (cnpj),
  INDEX idx_sr_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

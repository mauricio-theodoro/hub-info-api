CREATE TABLE captcha_challenges (
  id CHAR(36) NOT NULL PRIMARY KEY,
  service_request_id CHAR(36) NOT NULL,
  cnpj CHAR(14) NOT NULL,
  provider VARCHAR(20) NOT NULL,
  page_url VARCHAR(500) NOT NULL,
  site_key VARCHAR(120) NULL,
  status VARCHAR(20) NOT NULL,
  created_by_user_id CHAR(36) NULL,
  created_by_email VARCHAR(255) NULL,
  created_at DATETIME(6) NOT NULL,
  solved_at DATETIME(6) NULL,
  solution_token TEXT NULL,

  INDEX idx_captcha_service_request_id (service_request_id),
  INDEX idx_captcha_status (status)
);

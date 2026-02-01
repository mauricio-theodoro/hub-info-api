-- Cria tabela de desafios de CAPTCHA (hCaptcha) vinculados a uma ServiceRequest.
-- UUID armazenado como CHAR(36) para compatibilidade com preferred_uuid_jdbc_type=CHAR.

CREATE TABLE IF NOT EXISTS captcha_challenges (
    id CHAR(36) NOT NULL,
    service_request_id CHAR(36) NOT NULL,
    cnpj VARCHAR(14) NOT NULL,

    provider VARCHAR(32) NOT NULL,
    site_key VARCHAR(120) NOT NULL,
    page_url VARCHAR(500) NOT NULL,
    context_key VARCHAR(80) NULL,

    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,

    solved_at TIMESTAMP(6) NULL,
    solution_token VARCHAR(4000) NULL,

    created_by_user_id CHAR(36) NULL,
    created_by_email VARCHAR(120) NULL,

    PRIMARY KEY (id),
    KEY idx_captcha_challenges_service_request_id (service_request_id),
    KEY idx_captcha_challenges_status_created_at (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

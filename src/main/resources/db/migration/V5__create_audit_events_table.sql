CREATE TABLE IF NOT EXISTS audit_events (
  id               CHAR(36)      NOT NULL,
  event_type        VARCHAR(60)   NOT NULL,
  occurred_at       TIMESTAMP(6)  NOT NULL,

  actor_user_id     CHAR(36)      NULL,
  actor_email       VARCHAR(255)  NULL,

  request_ip        VARCHAR(45)   NULL,
  request_method    VARCHAR(10)   NULL,
  request_path      VARCHAR(255)  NULL,
  user_agent        VARCHAR(255)  NULL,

  success           BOOLEAN       NULL,

  target_type       VARCHAR(60)   NULL,
  target_id         CHAR(36)      NULL,

  details_json      JSON          NULL,

  PRIMARY KEY (id),
  INDEX idx_audit_occurred_at (occurred_at),
  INDEX idx_audit_event_type (event_type),
  INDEX idx_audit_actor_user (actor_user_id),
  INDEX idx_audit_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

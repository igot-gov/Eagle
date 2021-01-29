CREATE TABLE IF NOT EXISTS roles (
  id SERIAL PRIMARY KEY,
  role_name VARCHAR(64) UNIQUE NOT NULL,
  description TEXT
);

INSERT INTO roles (role_name, description) values ('SPV ADMIN', 'SPV Administrator'), ('MDO ADMIN', 'MDO Administrator'), ('EDITOR', 'CBP Editor'), ('CONTENT_CREATOR', 'CBP Content Creator'), ('REVIEWER', 'CBP Reviewer'), ('PUBLISHER', 'CBP Publisher'), ('Member','Portal Member');

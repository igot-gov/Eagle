CREATE TABLE bodhi.master_departments(
  id SERIAL,
  root_org VARCHAR(32),
  org VARCHAR(32),
  friendly_name VARCHAR(64) UNIQUE,
  description TEXT,
  PRIMARY KEY (id)
);

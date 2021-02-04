CREATE TABLE IF NOT EXISTS departments (
  id SERIAL PRIMARY KEY, 
  root_org VARCHAR(32) NOT NULL,
  dept_name VARCHAR(128) UNIQUE NOT NULL,
  dept_type_ids integer[] NOT NULL,
  description TEXT,
  headquarters VARCHAR(64),
  logo bytea,
  creation_date bigint,
  created_by text
);


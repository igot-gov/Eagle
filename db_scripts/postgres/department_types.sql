CREATE TABLE IF NOT EXISTS department_types (
  id SERIAL PRIMARY KEY,
  dept_type VARCHAR(64) NOT NULL,
  dept_subType VARCHAR(64) NOT NULL,
  description TEXT,
  UNIQUE (dept_type, dept_subType)
);

INSERT INTO department_types (dept_type, dept_subType, description) values ('SPV', 'Department', 'SPV Department'),  ('MDO', 'Department', 'MDO Department'), ('MDO', 'Board', 'MDO Board'), ('MDO', 'Ministry', 'MDO Ministry'), ('CBP', 'Govt. Affiliated', 'CBP Govt. Affiliated'), ('CBP', 'External', 'CBP External');



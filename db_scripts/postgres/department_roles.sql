CREATE TABLE IF NOT EXISTS department_roles (
  id SERIAL PRIMARY KEY,
  dept_type VARCHAR(64) NOT NULL,
  role_ids integer[] NOT NULL
);

INSERT INTO department_roles (dept_type, role_ids) values ('SPV', '{1,7}'), ('MDO','{2,7}'), ('CBP','{3,4,5,6}');

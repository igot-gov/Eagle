CREATE TABLE IF NOT EXISTS user_department_role (
  id SERIAL PRIMARY KEY,
  user_id TEXT NOT NULL, 
  dept_id int REFERENCES departments (id) NOT NULL,
  role_ids integer[] NOT NULL,
  isActive boolean NOT NULL,
  isBlocked boolean NOT NULL
);



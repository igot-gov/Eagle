CREATE TABLE IF NOT EXISTS bodhi.user_connection (
	root_org text,
	user_id text,
	connection_id text,
	connection_status text,
	connection_type text,
	end_on timestamp,
	start_on timestamp,
	PRIMARY KEY (root_org, user_id, connection_id)
);

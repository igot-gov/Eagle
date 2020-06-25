CREATE TABLE IF NOT EXISTS user_admin_level(
	root_org text,
	user_id text,
	level text,
	primary key((root_org,user_id),level)
);
CREATE TABLE bodhi.eagle_uuid_master (
	key uuid,
	email varchar,
	active boolean,
	firstname varchar,
	lastname varchar,
	PRIMARY KEY (email,key)
);

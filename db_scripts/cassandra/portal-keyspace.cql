CREATE KEYSPACE IF NOT exists portal WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE portal.sessions (
	sid varchar,
	expires timestamp,
	session varchar,
	PRIMARY KEY (sid)
);

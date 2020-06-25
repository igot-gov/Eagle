CREATE SEQUENCE IF NOT EXISTS batch.content_source_progress_seq START 10000;
ALTER SEQUENCE batch.content_source_progress_seq OWNER TO wingspan;


/* Hashed user id would be hashed emails which are required for matching with recalc table, since
   encrypted values of email ids will differ from time to time. */
CREATE TABLE IF NOT EXISTS batch.content_source_progress(
	id INTEGER DEFAULT NEXTVAL('batch.content_source_progress_seq') PRIMARY KEY,
	root_org TEXT,
	source_name TEXT,
	user_id BYTEA,
	user_id_type TEXT,
	hashed_user_id TEXT,
	content_id_at_source TEXT,
	content_id TEXT,
	content_type TEXT,
	first_activity_on TEXT,
	percent_complete TEXT,
	completed_on TEXT,
	status TEXT,
	reason TEXT,
	batch_date TIMESTAMP,
	retry_count INTEGER,
	updated_on TIMESTAMP,
	updated_by TEXT
);
ALTER TABLE batch.content_source_progress OWNER TO wingspan;

CREATE TABLE IF NOT EXISTS batch.progress_recalc_user_content(
	rec_type TEXT,
	id TEXT,
	updated_on TIMESTAMP,
	updated_by TEXT,
	CONSTRAINT progress_recalc_user_content_pkey PRIMARY KEY(rec_type, id)
);
ALTER TABLE batch.progress_recalc_user_content OWNER TO wingspan;
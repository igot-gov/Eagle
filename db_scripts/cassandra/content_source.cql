CREATE TABLE IF NOT EXISTS bodhi.content_source_user_registration (
	root_org text,
	user_id text,
	source_short_name text,
	end_date timestamp,
	start_date timestamp,
	started_on timestamp,
PRIMARY KEY ((root_org, source_short_name), user_id)
);
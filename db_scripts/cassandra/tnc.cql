CREATE TABLE IF NOT EXISTS bodhi.user_terms_condition (
	root_org text,
	user_id text,
	doc_name text,
	doc_for text,
	version double,
	accepted_on timestamp,
	language text,
    PRIMARY KEY ((root_org, user_id, doc_name, doc_for), version)
) WITH CLUSTERING ORDER BY (version DESC);
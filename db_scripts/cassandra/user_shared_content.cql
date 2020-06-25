create table if not exists user_shared_content(
	root_org text,
	user_id text,
	content_id text,
	shared_by text,
	shared_on timestamp,
	share_message text,
	primary key((root_org,user_id,content_id),shared_by)
);

	
CREATE MATERIALIZED VIEW bodhi.user_shared_content_date_shared AS
    SELECT root_org, user_id,shared_on,content_id,shared_by,share_message
    FROM bodhi.user_shared_content
    WHERE root_org IS NOT NULL AND user_id IS NOT NULL AND content_id IS NOT NULL AND shared_on IS NOT NULL and shared_by is NOT NULL
    PRIMARY KEY ((root_org, user_id),shared_on,content_id,shared_by) WITH CLUSTERING ORDER BY (shared_on DESC);
	
	
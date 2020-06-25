CREATE TABLE IF NOT EXISTS bodhi.user_badges (
	root_org text,
    user_id text,
    badge_id text,
    badge_type text,
    first_received_date timestamp,
    last_received_date timestamp,
    progress float,
    progress_date timestamp,
    received_count int,
    PRIMARY KEY ((root_org, user_id), badge_id)
) WITH CLUSTERING ORDER BY (badge_id ASC);
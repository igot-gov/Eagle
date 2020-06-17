CREATE TABLE IF NOT EXISTS bodhi.user_content_rating (
    root_org text,
    content_id text,
    user_id text,
    last_updated_on timestamp,
    rating float,
    PRIMARY KEY ((root_org, content_id), user_id)
) WITH CLUSTERING ORDER BY (user_id ASC);
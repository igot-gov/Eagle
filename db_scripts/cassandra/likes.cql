-- Likes table

CREATE TABLE IF NOT EXISTS bodhi.user_likes (
    root_org text,
    user_id text,
    content_id text,
    date_created timestamp,
    PRIMARY KEY ((root_org, user_id), content_id)
);

CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.user_likes_by_content AS
    SELECT root_org,content_id, user_id,  date_created
    FROM bodhi.user_likes
    WHERE root_org IS NOT NULL AND content_id IS NOT NULL AND user_id IS NOT NULL  AND date_created IS NOT NULL
    PRIMARY KEY ((root_org, content_id), user_id)
    WITH CLUSTERING ORDER BY (user_id desc);

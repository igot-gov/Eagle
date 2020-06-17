CREATE TABLE IF NOT EXISTS bodhi.user_content_progress (
    root_org text,
    user_id text,
    content_type text,
    content_id text,
    date_updated timestamp,
    first_accessed_on timestamp,
    first_completed_on timestamp,
    last_accessed_on timestamp,
    last_ts timestamp,
    progress float,
    updated_by text,
    visited_set set<float>,
    PRIMARY KEY ((root_org,user_id), content_type, content_id)
) WITH CLUSTERING ORDER BY (content_type ASC, content_id ASC);


CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.user_content_progress_by_date_updated AS
    SELECT root_org, date_updated, content_type, content_id, user_id, first_accessed_on, first_completed_on, last_accessed_on, last_ts, progress, updated_by
    FROM bodhi.user_content_progress
    WHERE root_org IS NOT NULL AND user_id IS NOT NULL AND content_type IS NOT NULL AND content_id IS NOT NULL AND date_updated IS NOT NULL
    PRIMARY KEY ((root_org, date_updated), content_type, content_id, user_id)
    WITH CLUSTERING ORDER BY (content_type ASC, content_id ASC, user_id ASC);



CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.user_content_progress_order_last_assess AS
    SELECT root_org,user_id, content_type, last_accessed_on, content_id, first_accessed_on, first_completed_on, last_ts, progress
    FROM bodhi.user_content_progress
    WHERE root_org IS NOT NULL and user_id IS NOT NULL AND content_type IS NOT NULL AND content_id IS NOT NULL AND last_accessed_on IS NOT NULL
    PRIMARY KEY ((root_org,user_id), content_type, last_accessed_on, content_id)
    WITH CLUSTERING ORDER BY (content_type ASC, last_accessed_on DESC, content_id ASC);



CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.user_content_progress_by_last_accessed_on AS
    SELECT root_org,content_id, last_accessed_on, user_id, content_type, first_accessed_on, first_completed_on, last_ts, progress
    FROM bodhi.user_content_progress
    WHERE root_org IS NOT NULL and user_id IS NOT NULL AND content_type IS NOT NULL AND content_id IS NOT NULL AND last_accessed_on IS NOT NULL
    PRIMARY KEY ((root_org,content_id), last_accessed_on, user_id, content_type)
    WITH CLUSTERING ORDER BY (last_accessed_on DESC, user_id ASC, content_type ASC);


CREATE TABLE IF NOT EXISTS bodhi.user_discrete_points (
    root_org text,
    user_id text,
    content_id text,
    given_on timestamp,
    points int,
    points_for text,
    PRIMARY KEY ((root_org,user_id), content_id)
) WITH CLUSTERING ORDER BY (content_id ASC);


CREATE TABLE IF NOT EXISTS bodhi.total_points (
    root_org text,
    user_id text,
    collaborative_points counter,
    learning_points counter,
    PRIMARY KEY ((root_org, user_id))
);
CREATE TABLE IF NOT EXISTS bodhi.continue_learning (
    root_org text,
    user_id text,
    context_path_id text,
    data text,
    date_accessed bigint,
    resource_id text,
    PRIMARY KEY ((root_org, user_id), context_path_id)
) WITH CLUSTERING ORDER BY (context_path_id ASC)
AND default_time_to_live = 15552000;


CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.mv_continue_learning AS
    SELECT root_org, user_id, date_accessed, context_path_id, data, resource_id
    FROM bodhi.continue_learning
    WHERE root_org IS NOT NULL AND user_id IS NOT NULL AND context_path_id IS NOT NULL AND date_accessed IS NOT NULL
    PRIMARY KEY ((root_org, user_id), date_accessed, context_path_id)
    WITH CLUSTERING ORDER BY (date_accessed DESC, context_path_id ASC);

CREATE TABLE IF NOT EXISTS bodhi.daily_time_spent (
     root_org text,
     user_id text,
     date timestamp,
     time_spent double,
     PRIMARY KEY ((root_org, user_id), date)
) WITH CLUSTERING ORDER BY (date ASC);


CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.daily_time_spent_by_date AS
     SELECT root_org, user_id, date, time_spent
     FROM bodhi.daily_time_spent
     WHERE root_org IS NOT NULL AND user_id IS NOT NULL AND date IS NOT NULL
     PRIMARY KEY ((root_org, date), user_id)
     WITH CLUSTERING ORDER BY (user_id ASC);



CREATE TABLE IF NOT EXISTS bodhi.daily_time_spent_monthly (
    root_org text,
    user_id text,
    date timestamp,
    time_spent double,
    PRIMARY KEY ((root_org, user_id), date)
) WITH CLUSTERING ORDER BY (date ASC) AND DEFAULT_TIME_TO_LIVE=3888000;

CREATE TABLE IF NOT EXISTS bodhi.leaderboard (
	root_org text,
    leaderboard_year int,
    duration_type text,
    duration_value int,
    leaderboard_type text,
    user_id text,
    end_date timestamp,
    points int,
    rank int,
    start_date timestamp,
    PRIMARY KEY ((root_org,leaderboard_year, duration_type, duration_value, leaderboard_type), user_id)
) WITH CLUSTERING ORDER BY (user_id ASC);


CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.leaderboard_rank AS
    SELECT root_org,rank, duration_type, leaderboard_type, leaderboard_year, duration_value, user_id, end_date, points, start_date
    FROM bodhi.leaderboard
    WHERE root_org is NOT NULL and leaderboard_year IS NOT NULL AND duration_type IS NOT NULL AND duration_value IS NOT NULL AND leaderboard_type IS NOT NULL AND rank IS NOT NULL AND user_id IS NOT NULL
    PRIMARY KEY ((root_org,rank), duration_type, leaderboard_type, leaderboard_year, duration_value, user_id)
    WITH CLUSTERING ORDER BY (duration_type ASC, leaderboard_type ASC, leaderboard_year DESC, duration_value DESC, user_id ASC);

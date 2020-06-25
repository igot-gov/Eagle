/* Live Events Table */ 
CREATE TABLE IF NOT EXISTS bodhi.live_events (
    root_org text,
	org text,
    event_name text,
    start_time timestamp,
    end_time timestamp,
    event_url text,
    PRIMARY KEY ((root_org, org, event_name), start_time)
) WITH CLUSTERING ORDER BY (start_time DESC);
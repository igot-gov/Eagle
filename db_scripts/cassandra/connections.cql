CREATE TABLE IF NOT EXISTS bodhi.connections (
    root_org text,
    user_id text,
    connection_id text,
    connection_status text,
    connection_type text,
    start_on timestamp,
    end_on timestamp,
    PRIMARY KEY (root_org, user_id, connection_id)
);


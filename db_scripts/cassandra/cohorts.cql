CREATE TABLE IF NOT EXISTS bodhi.educators (
    root_org text,
    content_id text,
    educator_id text,
    date_created timestamp,
    date_modified timestamp,
    PRIMARY KEY ((root_org,content_id), educator_id)
) WITH CLUSTERING ORDER BY (educator_id ASC);



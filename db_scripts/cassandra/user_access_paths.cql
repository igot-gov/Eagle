CREATE TABLE IF NOT EXISTS bodhi.user_access_paths (
    root_org text,
    org text,
    user_id uuid,
    cas_id uuid,
    access_paths set<text>,
    temporary boolean,
    ttl int,
    PRIMARY KEY ((root_org, org), user_id, cas_id)
) WITH CLUSTERING ORDER BY (user_id ASC, cas_id ASC);

 

CREATE INDEX index_user_access_paths_user_id ON bodhi.user_access_paths (user_id);
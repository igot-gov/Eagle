CREATE TABLE IF NOT EXISTS bodhi.user_interest (
    root_org text,
    user_id text,
    created_on timestamp,
    interest set<text>,
    updated_on timestamp,
    PRIMARY KEY ((root_org, user_id))
);
-- roles table 

CREATE TABLE IF NOT EXISTS bodhi.user_roles (
    root_org text,
    user_id text,
    roles list<text>,
    PRIMARY KEY ((root_org, user_id))
);
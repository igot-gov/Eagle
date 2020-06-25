
CREATE TABLE IF NOT EXISTS wingspan.group_user_mapping (
    root_org varchar,
    group_id varchar,
    user_id varchar,
    date_created timestamp,
    PRIMARY KEY (root_org, group_id, user_id)
);

ALTER TABLE wingspan.group_user_mapping OWNER TO wingspan;


CREATE TABLE IF NOT EXISTS wingspan.educator_group_mapping (
    root_org varchar,
    educator_id varchar,
    group_id varchar,
    date_created timestamp,
    group_name varchar,
    PRIMARY KEY (root_org, educator_id, group_id)
);

ALTER TABLE wingspan.educator_group_mapping  OWNER TO wingspan;
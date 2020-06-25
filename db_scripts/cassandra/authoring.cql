CREATE TABLE IF NOT EXISTS bodhi.content_work_flow (
    root_org text,
    org text,
    content_type text,
    flow_operations list<text>,
    work_flow list<text>,
    PRIMARY KEY (root_org, org, content_type)
);


CREATE TABLE IF NOT EXISTS bodhi.master_values (
    entity text,
    date_modified timestamp,
    root_org text,
    values list<text>,
    PRIMARY KEY (root_org, entity)
);


CREATE TABLE IF NOT EXISTS bodhi.master_values_v2 (
    entity text,
    date_modified timestamp,
    root_org text,
    values list<text>,
    strignify boolean,
    PRIMARY KEY (root_org, entity)
);
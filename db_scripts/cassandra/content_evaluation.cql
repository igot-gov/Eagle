CREATE TABLE IF NOT EXISTS bodhi.content_evaluation (
    root_org text,
    org text,
    content_id text,
    user_id text,
    role text,
    date text,
    header text,
    description text,
    items map<text,text>,
    PRIMARY KEY (root_org, org, content_id, user_id, header)
);

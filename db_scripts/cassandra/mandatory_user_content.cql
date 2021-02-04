CREATE TABLE if not exists bodhi.mandatory_user_content (
    root_org text,
    org text,
    content_id text,
    content_type text,
    minProgressForCompletion float,
    PRIMARY KEY (root_org, org, content_id)
);

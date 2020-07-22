CREATE TABLE IF NOT EXISTS bodhi.postion_competency_allocation (
    root_org text,
    org text,
    user_role text,
    competency text,
    level List<int>,
    PRIMARY KEY (root_org, org, user_role, competency)
);

CREATE TABLE IF NOT EXISTS bodhi.user_position_competency (
    root_org text,
    org text,
    user_id text,
    user_role text,
    user_competency text,
    user_level List<int>,
    delta List<int>,
    PRIMARY KEY (root_org, org, user_id, user_role, user_competency)
);

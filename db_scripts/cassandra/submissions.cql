CREATE TABLE IF NOT EXISTS  bodhi.educator_group_mapping (
    root_org text,
    educator_id text,
    group_id timeuuid,
    date_created timestamp,
    group_name text,
    PRIMARY KEY ((root_org,educator_id), group_id)
) WITH CLUSTERING ORDER BY (group_id DESC);


CREATE TABLE IF NOT EXISTS bodhi.group_user_mapping (
    root_org text,
    group_id timeuuid,
    user_id text,
    date_created timestamp,
    group_name text,
    PRIMARY KEY ((root_org,group_id), user_id)
) WITH CLUSTERING ORDER BY (user_id ASC);


CREATE TABLE IF NOT EXISTS bodhi.user_exercise (
    root_org text,
    user_id text,
    content_id text,
    submission_id timeuuid,
    feedback_by text,
    feedback_time timestamp,
    feedback_type text,
    feedback_url text,
    result_percent float,
    submission_time timestamp,
    submission_type text,
    submission_url text,
    testcases_failed int,
    testcases_passed int,
    total_testcases int,
    PRIMARY KEY ((root_org,user_id), content_id, submission_id)
) WITH CLUSTERING ORDER BY (content_id ASC, submission_id DESC);


CREATE TABLE IF NOT EXISTS bodhi.user_assessment_master (
    root_org text,
    ts_created timestamp,
    parent_source_id text,
    result_percent decimal,
    id uuid,
    correct_count int,
    date_created timestamp,
    incorrect_count int,
    not_answered_count int,
    parent_content_type text,
    pass_percent decimal,
    source_id text,
    source_title text,
    user_id text,
    PRIMARY KEY ((root_org,ts_created), parent_source_id, result_percent, id)
) WITH CLUSTERING ORDER BY (parent_source_id ASC, result_percent DESC, id DESC);


CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.user_assessment_top_performer AS
    SELECT root_org,parent_source_id, ts_created, result_percent, id, pass_percent, source_id, source_title, user_id
    FROM bodhi.user_assessment_master
    WHERE root_org is NOT NULL and ts_created IS NOT NULL AND parent_source_id IS NOT NULL AND id IS NOT NULL AND result_percent IS NOT NULL AND result_percent >= 90
    PRIMARY KEY ((root_org,parent_source_id), ts_created, result_percent, id)
    WITH CLUSTERING ORDER BY (ts_created DESC, result_percent DESC, id ASC);
	
	
CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.assessment_by_content_user AS
    SELECT root_org,user_id, parent_source_id, ts_created, result_percent, id, correct_count, incorrect_count, not_answered_count, pass_percent, source_id, source_title
    FROM bodhi.user_assessment_master
    WHERE root_org is NOT NULL and ts_created IS NOT NULL AND parent_source_id IS NOT NULL AND id IS NOT NULL AND result_percent IS NOT NULL AND user_id IS NOT NULL
    PRIMARY KEY ((root_org,user_id, parent_source_id), ts_created, result_percent, id)
    WITH CLUSTERING ORDER BY (parent_source_id ASC, ts_created DESC, result_percent DESC, id DESC);
	
	
	
CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.user_assessment_by_date AS
    SELECT root_org ,date_created, ts_created, parent_source_id, result_percent, id, parent_content_type, pass_percent, source_id, user_id
    FROM bodhi.user_assessment_master
    WHERE root_org is NOT NULL and date_created IS NOT NULL AND ts_created IS NOT NULL AND parent_source_id IS NOT NULL AND id IS NOT NULL AND result_percent IS NOT NULL
    PRIMARY KEY ((root_org ,date_created), ts_created, parent_source_id, result_percent, id)
    WITH CLUSTERING ORDER BY (ts_created DESC, parent_source_id ASC, result_percent DESC, id DESC);

	
CREATE TABLE IF NOT EXISTS bodhi.user_exercise_last (
    root_org text,
    user_id text,
    content_id text,
    feedback_by text,
    feedback_submission_id timeuuid,
    feedback_time timestamp,
    feedback_type text,
    feedback_url text,
    result_percent float,
    submission_id timeuuid,
    submission_time timestamp,
    submission_type text,
    submission_url text,
    testcases_failed int,
    testcases_passed int,
    total_testcases int,
    PRIMARY KEY ((root_org,user_id), content_id)
) WITH CLUSTERING ORDER BY (content_id ASC);


CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.user_exercise_last_by_feedback AS
    SELECT root_org,user_id, feedback_time, content_id, feedback_by, feedback_submission_id, submission_id
    FROM bodhi.user_exercise_last
    WHERE root_org IS NOT NULL AND user_id IS NOT NULL AND content_id IS NOT NULL AND feedback_time IS NOT NULL
    PRIMARY KEY ((root_org,user_id), feedback_time, content_id)
    WITH CLUSTERING ORDER BY (feedback_time DESC, content_id DESC);


CREATE TABLE IF NOT EXISTS bodhi.user_assessment_summary (
    root_org text,
    user_id text,
    content_id text,
    first_passed_score float,
    first_passed_score_date timestamp,
    max_score float,
    max_score_date timestamp,
    PRIMARY KEY ((root_org, user_id), content_id)
) WITH CLUSTERING ORDER BY (content_id ASC);


CREATE TABLE IF NOT EXISTS bodhi.user_quiz_master (
    root_org text,
    ts_created timestamp,
    result_percent decimal,
    id uuid,
    correct_count int,
    date_created timestamp,
    incorrect_count int,
    not_answered_count int,
    pass_percent decimal,
    source_id text,
    source_title text,
    user_id text,
    PRIMARY KEY ((root_org, ts_created), result_percent, id)
) WITH CLUSTERING ORDER BY (result_percent DESC, id DESC);


CREATE TABLE IF NOT EXISTS bodhi.user_quiz_summary (
    root_org text,
    user_id text,
    content_id text,
    date_updated timestamp,
    PRIMARY KEY ((root_org, user_id), content_id)
) WITH CLUSTERING ORDER BY (content_id ASC);

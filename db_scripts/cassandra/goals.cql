/*	GOALS TABLE SCRIPT STARTS	*/ 													


/* user_learning_goals Creation Script */

CREATE TABLE IF NOT EXISTS bodhi.user_learning_goals (
    root_org text,
    user_id text,
    goal_type text,
    goal_id uuid,
    created_on timestamp,
    goal_content_id list<text>,
    goal_desc text,
    goal_duration int,
    goal_end_date timestamp,
    goal_start_date timestamp,
    goal_title text,
    last_updated_on timestamp,
    version float,
    PRIMARY KEY ((root_org, user_id, goal_type), goal_id)
);


/* Index on goal_content_id field */
CREATE INDEX IF NOT EXISTS idx_userlearninggoals ON bodhi.user_learning_goals (goal_content_id);


/* user_shared_goals Creation Script */
CREATE TABLE IF NOT EXISTS bodhi.user_shared_goals (
    root_org text,
    shared_with text,
    goal_type text,
    goal_id uuid,
    shared_by text,
    goal_content_id list<text>,
    goal_desc text,
    goal_duration int,
    goal_end_date timestamp,
    goal_start_date timestamp,
    goal_title text,
    last_updated_on timestamp,
    shared_on timestamp,
    status int,
    status_message text,
    version float,
    PRIMARY KEY ((root_org, shared_with), goal_type, goal_id, shared_by)
);


/* Index on status field */
CREATE INDEX IF NOT EXISTS idx_usersharedgoalsstatus ON bodhi.user_shared_goals (status);


/* user_shared_goals mv  - used to query if records are to be fetched by shared_by*/
CREATE MATERIALIZED VIEW IF NOT EXISTS bodhi.mv_shared_goals_tracker AS
    SELECT root_org,shared_by, goal_type, goal_id, shared_with, goal_content_id, goal_desc, goal_duration, goal_end_date, goal_start_date, shared_on, goal_title, last_updated_on, status, status_message, version
    FROM bodhi.user_shared_goals
    WHERE root_org IS NOT NULL AND shared_by IS NOT NULL AND goal_id IS NOT NULL AND goal_type IS NOT NULL AND shared_with IS NOT NULL
    PRIMARY KEY ((root_org, shared_by), goal_type, goal_id, shared_with);

/*	GOALS TABLE SCRIPT ENDS	*/ 		


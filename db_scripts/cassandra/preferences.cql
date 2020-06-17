/* DROP user_preferences table */

CREATE TABLE IF NOT EXISTS bodhi.user_preferences (
    root_org text,
    user_id text,
    date_updated timestamp,
    preference_data text,
    PRIMARY KEY ((root_org, user_id))
);
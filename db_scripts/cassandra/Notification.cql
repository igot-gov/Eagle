CREATE TABLE IF NOT EXISTS bodhi.user_notification (
    root_org text,
    user_id text,
    classified_as text,
    notification_id uuid,
    event_id text,
    group_id text,
    message text,
    received_on timestamp,
    receiving_role text,
    seen boolean,
    seen_on timestamp,
    subject text,
    target_data text,
    PRIMARY KEY ((root_org, user_id, classified_as), notification_id)
) WITH CLUSTERING ORDER BY (notification_id DESC)
    AND default_time_to_live = 7890000;
    
CREATE INDEX idx_notification_seen ON bodhi.user_notification (seen);


CREATE TABLE IF NOT EXISTS bodhi.user_notification_event (
    root_org text,
    user_id text,
    receiving_role text,
    event_id text,
    event_activated boolean,
    group_id text,
    modes_activated map<text, boolean>,
    updated_on timestamp,
    PRIMARY KEY ((root_org, user_id, receiving_role), event_id)
);



CREATE TABLE IF NOT EXISTS bodhi.notification_errors (
    root_org text,
    event_id text,
    error_message text,
    request_body text,
    stack_trace text,
    PRIMARY KEY (root_org, event_id)
);



insert into bodhi.app_config (root_org,key,remarks,value) values ('root_org','invalid_domains','domains to which emails are not allowed','demo.com');




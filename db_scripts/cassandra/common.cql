CREATE TABLE IF NOT EXISTS bodhi.app_config (
    root_org text,
    key text,
    remarks text,
    value text,
    PRIMARY KEY ((root_org, key))
);

INSERT INTO bodhi.app_config json '{"root_org": "org_name", "key": "mac_for_external", "remarks": "mark as complete allowed for external content", "value": "false"}';

INSERT INTO bodhi.app_config json '{"root_org": "org_name", "key": "mac_for_instructor_led", "remarks": "mark as complete allowed for instructor-led content", "value": "false"}';

INSERT INTO bodhi.app_config json '{"root_org": "org_name", "key": "cohorts_activeuser_filter_duration", "remarks": "the duration by which the cohorts active user is filtered", "value": "15"}';

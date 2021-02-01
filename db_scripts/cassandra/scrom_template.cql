CREATE TABLE bodhi.scrom_template (
    root_org text,
    org text,
    contentid text,
    userid text,
    cmi_core_exit text,
    cmi_core_lesson_status text,
    cmi_core_session_time text,
    cmi_suspend_data text,
    errors list<text>,
    initialized boolean,
    type text,
    PRIMARY KEY (root_org, org, contentid, userid)
);

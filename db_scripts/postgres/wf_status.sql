CREATE TABLE if not exists wingspan.wf_status
(   
    wf_id character varying(64) NOT NULL,
    application_id character varying(64) NOT NULL,
    userid character varying(64) NOT NULL,
    in_workflow boolean,
    service_name character varying(64),
    actor_uuid character varying(64),
    created_on timestamp without time zone,
    current_status character varying(64),
    lastupdated_on timestamp without time zone,
    org character varying(64) NOT NULL,
    root_org character varying(64) NOT NULL,
    update_field_values character varying(1024),
    CONSTRAINT userprofile_wf_status_pkey PRIMARY KEY (wf_id)
);

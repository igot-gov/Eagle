CREATE TABLE wingspan.wf_audit
(
    id integer NOT NULL DEFAULT nextval('wingspan.wf_audit_id_seq'::regclass),
    wf_id character varying(64),
    application_id character varying(64) NOT NULL,
    actor_uuid character varying(64),
    service_name character varying(64),
    update_field_values character varying(1024),
    comment character varying(1024),
    created_on timestamp without time zone,
    action character varying(64),
    state character varying(64),
    root_org character varying(64),
    user_id character varying(64),
    in_workflow boolean,
    CONSTRAINT user_profile_wf_audit_pkey PRIMARY KEY (id)
);


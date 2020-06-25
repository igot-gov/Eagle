CREATE TABLE IF NOT EXISTS wingspan.content_source
(
    root_org character varying  NOT NULL,
    source_short_name character varying  NOT NULL,
	source_name character varying  NOT NULL,
	source_url character varying ,
    registration_url character varying ,
    progress_provided boolean,
	registration_provided boolean,
    registration_enabled boolean not null,
    license_expires_on timestamp without time zone,
    updated_on timestamp without time zone,
    updated_by character varying ,
    CONSTRAINT content_source_pkey PRIMARY KEY (root_org, source_short_name)
);

ALTER TABLE wingspan.content_source OWNER TO wingspan;
alter table wingspan.content_source add column registration_enabled boolean;
alter table wingspan.content_source alter column  registration_enabled set not null ;




CREATE SCHEMA IF NOT EXISTS social;

ALTER SCHEMA social OWNER TO wingspan;

CREATE TABLE social.userpollactivity
(
    root_org character varying NOT NULL,
    org character varying NOT NULL,
    user_id uuid NOT NULL,
    post_id uuid NOT NULL,
    activity_date timestamp(6) without time zone NOT NULL,
    is_voted boolean NOT NULL,
    option_id integer NOT NULL,
    id uuid,
    CONSTRAINT userpollactivity_pkey PRIMARY KEY (user_id, post_id)
)

ALTER TABLE social.userpollactivity
    OWNER to wingspan;
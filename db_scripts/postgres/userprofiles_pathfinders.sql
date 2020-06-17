CREATE SCHEMA pathfinders; 
CREATE TABLE pathfinders.user_profile
(
    user_id uuid NOT NULL,
    role character varying(100) NOT NULL,
    role_privacy boolean,
    teaching_state character varying(25) NOT NULL,
    teaching_state_privacy boolean,
    organization character varying(250),
    organization_privacy boolean,
    profile_image character varying(250),
    phone character varying(15)[],
    phone_privacy boolean,
    public_profiles json[],
    created_on timestamp without time zone NOT NULL,
    created_by character varying(100) NOT NULL,
    last_updated_on timestamp without time zone NOT NULL,
    last_updated_by character varying(100) NOT NULL,
    status character varying(10) NOT NULL,
    user_name character varying(50) NOT NULL,
    emailprivacy boolean,
    CONSTRAINT pk_user_profile PRIMARY KEY (user_id),
    CONSTRAINT uk_user_profile UNIQUE (user_id)
);

CREATE TABLE pathfinders.user_profile_history
(
    history_id uuid NOT NULL,
    user_id uuid NOT NULL,
    role character varying(100) NOT NULL,
    role_privacy boolean,
    teaching_state character varying(25),
    teaching_state_privacy boolean,
    organization character varying(250),
    organization_privacy boolean,
    profile_image character varying(250),
    phone character varying(15)[],
    phone_privacy boolean,
    public_profiles json[],
    created_on timestamp without time zone NOT NULL,
    created_by character varying(100) NOT NULL,
    updated_on timestamp without time zone NOT NULL,
    updated_by character varying(100) NOT NULL,
    status character varying(10) NOT NULL,
    user_name character varying(50) NOT NULL,
    emailprivacy boolean,
    CONSTRAINT pk_user_profile_history PRIMARY KEY (history_id, user_id)
);

CREATE TABLE pathfinders.state
(
    state character varying(40) NOT NULL,
    state_code character varying(4) NOT NULL,
    country character varying(30) NOT NULL,
    country_code character varying(4) NOT NULL,
    CONSTRAINT state_pkey PRIMARY KEY (state_code, country_code)
);


alter table pathfinders.user_profile owner to wingspan;
alter table pathfinders.user_profile_history owner to wingspan;
alter table pathfinders.state owner to wingspan;
alter SCHEMA pathfinders owner to wingspan;

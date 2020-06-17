CREATE TABLE IF NOT EXISTS wingspan.user_details
(
    emp_number integer NOT NULL,
    email text COLLATE pg_catalog."default",
    name text COLLATE pg_catalog."default" NOT NULL,
    status character(1) COLLATE pg_catalog."default" NOT NULL,
    onsite_offshore_indicator text COLLATE pg_catalog."default",
    company text COLLATE pg_catalog."default" NOT NULL,
    job_level text COLLATE pg_catalog."default",
    current_city text COLLATE pg_catalog."default",
    ibu_code text COLLATE pg_catalog."default",
    pu_code text COLLATE pg_catalog."default",
    cu_code text COLLATE pg_catalog."default",
    master_customer_code text COLLATE pg_catalog."default",
    customer_code text COLLATE pg_catalog."default",
    master_project_code text COLLATE pg_catalog."default",
    project_code text COLLATE pg_catalog."default",
    joining_date timestamp without time zone,
    is_download_allowed boolean,
    CONSTRAINT user_details_pkey PRIMARY KEY (emp_number),
    CONSTRAINT user_details_email_key UNIQUE (email)

);

ALTER TABLE wingspan.user_details OWNER to wingspan;
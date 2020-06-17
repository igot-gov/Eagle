CREATE TABLE IF NOT EXISTS wingspan.trending
(
    root_org text COLLATE pg_catalog."default" NOT NULL,
    content_id text COLLATE pg_catalog."default" NOT NULL,
    total_hits integer NOT NULL,
    created_on date NOT NULL,
    CONSTRAINT trending_pkey PRIMARY KEY (root_org, content_id, created_on)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wingspan.trending
    OWNER to wingspan;
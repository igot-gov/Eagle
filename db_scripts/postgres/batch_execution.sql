CREATE SCHEMA IF NOT EXISTS batch;

ALTER SCHEMA batch OWNER TO wingspan;

CREATE TABLE batch.batch_execution
(
    root_org CHARACTER VARYING,
    batch_name CHARACTER VARYING,
    started_on TIMESTAMP,
    ended_on TIMESTAMP,
    batch_date TIMESTAMP,
    data_start_ts TIMESTAMP,
    data_end_ts TIMESTAMP,
    txn_count INTEGER,
    success_count INTEGER,
    failed_count INTEGER,
    skip_count INTEGER,
    status CHARACTER VARYING,
    CONSTRAINT batch_execution_pkey PRIMARY KEY (root_org, batch_name, started_on)
);

ALTER TABLE batch.batch_execution
    OWNER to wingspan;
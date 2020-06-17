Create SCHEMA IF NOT EXISTS wingspan;

ALTER SCHEMA wingspan OWNER TO wingspan;

CREATE TABLE IF NOT EXISTS wingspan.terms_and_conditions (
    root_org text,
    doc_name text,
    doc_for text,
    version numeric(5,2),
    language text,
    document text not null,
    created_on timestamp not null,
    PRIMARY KEY (root_org, doc_name, doc_for, version, language)
);

ALTER TABLE wingspan.terms_and_conditions OWNER TO wingspan;

INSERT INTO wingspan.terms_and_conditions VALUES('eagle', 'Generic T&C', 'User', 1.0, 'en', 'T&C test', CURRENT_TIMESTAMP);

INSERT INTO wingspan.terms_and_conditions VALUES('eagle', 'Data Privacy', 'User', 1.0, 'en', 'DP test', CURRENT_TIMESTAMP);


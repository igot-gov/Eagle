CREATE TABLE IF NOT EXISTS wingspan.translated_field (
  id VARCHAR PRIMARY KEY,
  root_org VARCHAR NOT NULL,
  org VARCHAR NOT NULL,
  language VARCHAR NOT NULL,
  field VARCHAR NOT NULL,	
  translated_field VARCHAR NOT NULL,
  CONSTRAINT rootOrg_org_field_lang_unique UNIQUE (root_org, org, field, language)
);

CREATE TABLE IF NOT EXISTS wingspan.translated_value (
  value VARCHAR NOT NULL,
  translated_value VARCHAR NOT NULL,
  field_meta VARCHAR REFERENCES wingspan.translated_field(id),
  CONSTRAINT field_value_pk PRIMARY KEY (field_meta, value)
);

ALTER TABLE wingspan.translated_value OWNER TO wingspan;

ALTER TABLE wingspan.translated_field OWNER TO wingspan;


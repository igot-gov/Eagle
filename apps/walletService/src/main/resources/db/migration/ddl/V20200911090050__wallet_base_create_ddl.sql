CREATE TABLE IF NOT EXISTS user_wallet
(
  id character varying(64) NOT NULL,
  wid character varying(64) NOT NULL,
  wallet_type character varying(256) NOT NULL,
  created_by character varying(64),
  creation_time bigint,
  UNIQUE (wid, wallet_type),
  CONSTRAINT user_wallet_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS wallet
(
  id character varying(64) NOT NULL,
  user_wallet_id character varying(64) NOT NULL,
  expiry_date bigint,
  number_of_coins numeric,
  created_by character varying(64),
  creation_time bigint,
  updated_by character varying(64),
  updated_time bigint,
  CONSTRAINT wallet_pkey PRIMARY KEY (id),
  CONSTRAINT fk_user_wallet_id FOREIGN KEY (user_wallet_id)
      REFERENCES user_wallet (id)
);

CREATE TABLE IF NOT EXISTS wallet_ledger
(
  id character varying(64) NOT NULL,
  from_user_wallet_id character varying(64) NOT NULL,
  to_user_wallet_id character varying(64) NOT NULL,
  number_of_coins numeric,
  comments text,
  created_by character varying(64),
  creation_time bigint,
  CONSTRAINT wallet_ledger_pkey PRIMARY KEY (id),
  CONSTRAINT fk_from_user_wallet_id FOREIGN KEY (from_user_wallet_id)
      REFERENCES user_wallet (id),
  CONSTRAINT fk_to_user_wallet_id FOREIGN KEY (to_user_wallet_id)
      REFERENCES user_wallet (id)
);

CREATE TABLE IF NOT EXISTS wallet_audit
(
  id character varying(64) NOT NULL,
  user_wallet_id character varying(64) NOT NULL,
  expiry_date bigint,
  number_of_coins numeric,
  created_by character varying(64),
  creation_time bigint,
  updated_by character varying(64),
  updated_time bigint,
  CONSTRAINT wallet_audit_pkey PRIMARY KEY (id),
  CONSTRAINT fk_user_wallet_id FOREIGN KEY (user_wallet_id)
      REFERENCES user_wallet (id)
);

CREATE INDEX IF NOT EXISTS index_user_wallet_wid ON user_wallet (wid);
CREATE INDEX IF NOT EXISTS index_wallet_expiry_date ON wallet (expiry_date);

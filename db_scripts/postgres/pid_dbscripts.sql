---- Execute the commands inside PID User login in POSTGRES-----------

-------------- ENUM CREATION START --------------
DROP TYPE IF EXISTS imported_source_names;
CREATE TYPE imported_source_names AS ENUM ('DEVELOPER');
ALTER type imported_source_names add value 'AUTO_INSERT_VIA_API';
--------------- ENUM CREATION END ---------------

-------------- TABLES CREATION START --------------
-- Creating the client list used for authorization
DROP TABLE IF EXISTS pid_clients;
CREATE TABLE pid_clients(
  client_name VARCHAR(32),
  passphrase VARCHAR(64),
  PRIMARY KEY (client_name)
);

-- Creating the root org and org TABLE
DROP TABLE IF EXISTS pid_db_config;
CREATE TABLE pid_db_config(
  root_org VARCHAR(32),
  org VARCHAR(32),
  region VARCHAR(32),
  connection_string text
);

-- Creating the root org to domain mapping TABLE
DROP TABLE IF EXISTS root_org_domain_mapping;
CREATE TABLE root_org_domain_mapping (
  id SERIAL,
  root_org VARCHAR(32),
  domain_name VARCHAR(64)
);
--- Inserting te values
INSERT INTO root_org_domain_mapping(root_org, domain_name) VALUES ('igot', 'eagle-sb.idc.tarento.com');

-- Meta data table for root_org and orgs
DROP TABLE IF EXISTS org_details;
CREATE TABLE org_details (
  id SERIAL,
  root_org VARCHAR(32),
  org VARCHAR(32),
  domain_name VARCHAR(64),
  auto_save_email_prefix TEXT,
  auto_save_email_suffix TEXT,
  UNIQUE(root_org, org, domain_name)
);

INSERT INTO org_details (root_org, org, domain_name, auto_save_email_prefix, auto_save_email_suffix) values ('igot', 'dopt', 'eagle-sb.idc.tarento.com',  '', '@tarento.com');

-- Creating the extension for uuid generation
create extension "uuid-ossp";

-- Creating the user TABLE
DROP TABLE if exists wingspan_user;
CREATE TABLE wingspan_user (
  wid uuid DEFAULT uuid_generate_v4(),
  root_org VARCHAR(32),
  org VARCHAR(32),
  is_active BOOLEAN,
  account_expiry_date DATE,
  kid uuid UNIQUE,
  imported_source_name imported_source_names,
  source_id VARCHAR(64),
  username VARCHAR(64),
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  middle_name VARCHAR(100),
  known_as VARCHAR(100),
  salutation VARCHAR(10),
  email VARCHAR(64) UNIQUE,
  gender VARCHAR(16),
  dob DATE,
  languages_known VARCHAR(16) ARRAY,
  preferred_language VARCHAR(16), 
  source_profile_picture TEXT,
  residence_country VARCHAR(128),
  residence_state VARCHAR(64),
  residence_city VARCHAR(64),
  contact_phone_number_office VARCHAR(32), 
  contact_phone_number_home VARCHAR(32),
  contact_phone_number_personal VARCHAR(32),
  employment_status VARCHAR(32),
  contract_type VARCHAR(32),
  job_title VARCHAR(32),
  job_role VARCHAR(32),
  department_name TEXT,
  sub_department_name TEXT,
  unit_name VARCHAR(128),
  organization_location_country VARCHAR(128),
  organization_location_state VARCHAR(32),
  organization_location_city VARCHAR(32),
  time_inserted timestamp DEFAULT now(),
  time_updated timestamp,
  json_unmapped_fields JSON,
  source_data JSON
);

ALTER TABLE wingspan_user ADD PRIMARY KEY (wid);

-- Groups master TABLE
CREATE TABLE master_groups(
  id SERIAL,
  root_org VARCHAR(32),
  org VARCHAR(32),
  friendly_name VARCHAR(64) UNIQUE,
  description TEXT,
  PRIMARY KEY (id)
);

-- Creating table for groups
CREATE TABLE user_groups(
  user_wid uuid REFERENCES wingspan_user(wid) ON DELETE CASCADE,
  group_id INTEGER REFERENCES master_groups(id) ON DELETE CASCADE,
  UNIQUE (user_wid, group_id)
);

-- Table for saving email sent details for a mail account.
CREATE TABLE idp_forgot_password_mails(
  email VARCHAR(64),
  time_inserted timestamp DEFAULT now(),
  token_used TEXT
);

--------------- TABLES CREATION END ---------------

---------- FUNCTIONS -----------
-- TRIGGER to update the date on insert of wingspan user
CREATE OR REPLACE FUNCTION update_date_updated_column() 
RETURNS TRIGGER AS $$
BEGIN
    NEW.time_updated = now();
    RETURN NEW; 
END;
$$ language 'plpgsql';
-------------- END FUNCTIONS --------------------

-----------------START TRIGGERS--------------------------
-- Adding the TRIGGER to the table to update the date
DROP TRIGGER IF EXISTS trgr_date_update on wingspan_user;
CREATE TRIGGER trgr_date_update
  BEFORE UPDATE OR INSERT
  ON wingspan_user 
  FOR EACH ROW 
EXECUTE PROCEDURE update_date_updated_column();

------------------END TRIGGERS---------------------------

--------- PRIVILEGES ----------------
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public to pid;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public to pid;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public to pid;

------------ PRIVILEGES END --------------------------------

----------------- ALTER COMMANDS --------------------------
-- Adding column for storing user properties
DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE wingspan_user ADD COLUMN user_properties JSON;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column user_properties already exists in wingspan_user.';
        END;
    END;
$$;

-- Adding column to store the manager_id -----
DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE wingspan_user ADD COLUMN manager_id VARCHAR(64);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column manager_id already exists in wingspan_user.';
        END;
    END;
$$;

-- Adding column to store the timezone -----
DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE wingspan_user ADD COLUMN time_zone VARCHAR(16);
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column time_zone already exists in wingspan_user.';
        END;
    END;
$$;

-- Adding column to store the sub_department_name -----
DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE wingspan_user ADD COLUMN sub_department_name TEXT;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column sub_department_name already exists in wingspan_user.';
        END;
    END;
$$;

-- Adding column to store the socail media -----
DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE wingspan_user ADD COLUMN social_media_profiles JSONB;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column social_media_profiles already exists in wingspan_user.';
        END;
    END;
$$;

-- Adding the autocomplete filters coulumn which will be moved to autocomplete table on entry of wn-user
DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE wingspan_user ADD COLUMN autocomplete_filters JSONB;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column autocomplete_filters already exists in wingspan_user.';
        END;
    END;
$$;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public to pid;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public to pid;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public to pid;



insert into wingspan_user(first_name,last_name, email, root_org, org) values ('admin', 'admin', 'admin@sunbird.org', 'igot', 'dopt');

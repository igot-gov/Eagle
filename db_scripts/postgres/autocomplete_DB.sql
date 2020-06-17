
/**** IMPORTANT
RUN THIS SCRIPT INSIDE PID DATABASE
**//


-- For creating the autocomplete search using postgres
drop table if exists user_autocomplete;
create table user_autocomplete(
-- id serial primary key,
 first_name varchar,
 last_name varchar,
 email varchar UNIQUE,
 department_name varchar,
 wid uuid,
 kid uuid,
 root_org varchar,
 user_search tsvector
);
 
-- Adding the extra column for source_id
DO $$ 
BEGIN
BEGIN
ALTER TABLE user_autocomplete ADD COLUMN source_id VARCHAR(64);
 EXCEPTION
WHEN duplicate_column THEN RAISE NOTICE 'column source_id already exists in wingspan_user.';
END;
END;
$$;
 
-- Adding the filters
DO $$ 
BEGIN
BEGIN
ALTER TABLE user_autocomplete ADD COLUMN filters JSONB;
 EXCEPTION
WHEN duplicate_column THEN RAISE NOTICE 'column filters already exists in wingspan_user.';
END;
END;
$$;
 
-- Create funtiON to save record INTO autocomplete when there is an entry in wingspan_user
CREATE OR REPLACE FUNCTION update_user_autocomplete()RETURNS TRIGGER AS $$
BEGIN
INSERT INTO user_autocomplete(first_name, last_name, email, department_name, wid, kid, root_org, source_id, filters) values (
 new.first_name,
 new.last_name,
 new.email,
 new.department_name,
 new.wid,
 new.kid,
 new.root_org,
 new.source_id,
 new.autocomplete_filters
 ) ON conflict (email)
 do update
set (first_name,last_name,email,department_name,wid,kid,root_org,source_id,filters)=(new.first_name,new.last_name,new.email,new.department_name,new.wid,new.kid,new.root_org,new.source_id,new.autocomplete_filters);
RETURN NEW;
END
$$ LANGUAGE plpgsql;
 
-- On deleting te user from wingspan_user delete the entry from autocomplete as well
CREATE OR REPLACE FUNCTION delete_user_autocomplete()RETURNS TRIGGER AS $$
BEGIN
DELETE FROM user_autocomplete WHERE user_autocomplete.wid = OLD.wid;
RETURN OLD;
END $$ LANGUAGE'plpgsql';
 
-- Decide which fields to index ON and priority for them. Only 4 ranks are alowed A,B,C,D
CREATE OR REPLACE FUNCTION update_user_search() RETURNS TRIGGER AS $$
BEGIN
 NEW.user_search :=
 setweight(to_tsvector('simple', coalesce(NEW.source_id,'')), 'A') ||
 setweight(to_tsvector('simple', coalesce(NEW.first_name,'')), 'A') ||
 setweight(to_tsvector('simple', coalesce(NEW.last_name,'')), 'A') ||
     setweight(to_tsvector('simple', coalesce(NEW.email,'')), 'B') ||
 setweight(to_tsvector('simple', coalesce(NEW.department_name,'')), 'C');
RETURN NEW;
END
$$ LANGUAGE plpgsql;
 
-- Create trigger to insert into autocomplete table
DROP TRIGGER update_autocomplete ON wingspan_user;
CREATE TRIGGER update_autocomplete BEFORE INSERT OR UPDATE
ON wingspan_user FOR EACH ROW EXECUTE PROCEDURE update_user_autocomplete();
 
-- Creating trigger to delete frmo autocomplete
DROP TRIGGER delete_autocomplete ON wingspan_user;
CREATE TRIGGER delete_autocomplete BEFORE DELETE
ON wingspan_user FOR EACH ROW EXECUTE PROCEDURE delete_user_autocomplete();
 
-- Create trigger 
DROP TRIGGER user_search ON user_autocomplete;
CREATE TRIGGER user_search BEFORE INSERT OR UPDATE
ON user_autocomplete FOR EACH ROW EXECUTE PROCEDURE update_user_search();
 
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public to pid;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public to pid;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public to pid;


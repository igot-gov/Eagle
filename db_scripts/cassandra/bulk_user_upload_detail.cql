CREATE TABLE bodhi.bulk_user_upload_detail
(id uuid,
name varchar,
user_id uuid,
status varchar,
report blob,
PRIMARY KEY (id));

CREATE TABLE IF NOT EXISTS wingspan.user_roles (
    root_org text,
    user_id text,
    role text,
    updated_on timestamp NOT NULL,
    updated_by text NOT NULL,
    CONSTRAINT user_roles_pk PRIMARY KEY (root_org, user_id, role)
);

ALTER TABLE wingspan.user_roles OWNER TO wingspan;

insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'defaultuser', 'author', CURRENT_TIMESTAMP, '');
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','content-feedback-admin', CURRENT_TIMESTAMP, '');  
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','editor', CURRENT_TIMESTAMP, '');  
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','developer', CURRENT_TIMESTAMP, '');  
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','superAdmin', CURRENT_TIMESTAMP, '');  
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','publisher', CURRENT_TIMESTAMP, '');  
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','reviewer', CURRENT_TIMESTAMP, '');  
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','admin', CURRENT_TIMESTAMP, '');
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','privileged', CURRENT_TIMESTAMP, '');
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','channel-creator-advanced', CURRENT_TIMESTAMP, '');
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','channel-creator', CURRENT_TIMESTAMP, '');
insert into wingspan.user_roles (root_org, user_id, role, updated_on, updated_by) values ('eagle', 'masteruser','social', CURRENT_TIMESTAMP, '');
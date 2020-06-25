~
---Drop commands
DROP TABLE IF EXISTS wingspan.event_group;

DROP TABLE IF EXISTS wingspan.event_parameters;

DROP TABLE IF EXISTS wingspan.event_recipient;

DROP TABLE IF EXISTS wingspan.parameter_language;

DROP TABLE IF EXISTS wingspan.tbl_tenant_mode;

-- DROP TABLE IF EXISTS wingspan.smtp_config;

DROP TABLE IF EXISTS wingspan.sns_notification_config;

DROP TABLE IF EXISTS wingspan.tenant_mode_language;

DROP TABLE IF EXISTS wingspan.tenant_template_footer;

DROP TABLE IF EXISTS wingspan.tenant_event_notification;

DROP TABLE IF EXISTS wingspan.tenant_event_template;

DROP TABLE IF EXISTS wingspan.recipient_description;








CREATE TABLE IF NOT EXISTS wingspan.event_group
(
    event_id character varying COLLATE pg_catalog."default" NOT NULL,
    language character varying COLLATE pg_catalog."default" NOT NULL,
    event_name character varying COLLATE pg_catalog."default" NOT NULL,
    group_id character varying COLLATE pg_catalog."default" NOT NULL,
    group_name character varying COLLATE pg_catalog."default" NOT NULL,
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    event_description character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT event_group_prim_key PRIMARY KEY (event_id, language)
);


ALTER TABLE wingspan.event_group
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.event_parameters
(
    root_org character varying COLLATE pg_catalog."default" NOT NULL,
    org character varying COLLATE pg_catalog."default" NOT NULL,
    event_id character varying COLLATE pg_catalog."default" NOT NULL,
    parameter_id character varying COLLATE pg_catalog."default",
    value character varying COLLATE pg_catalog."default",
    CONSTRAINT event_param_pkey PRIMARY KEY (root_org, org, event_id)
);

ALTER TABLE wingspan.event_parameters
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.event_recipient
(
    event_id character varying COLLATE pg_catalog."default" NOT NULL,
    recipient character varying COLLATE pg_catalog."default" NOT NULL,
    tag character varying COLLATE pg_catalog."default" NOT NULL,
    target_url character varying COLLATE pg_catalog."default",
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    classification character varying COLLATE pg_catalog."default",
    user_roles character varying COLLATE pg_catalog."default",
    CONSTRAINT event_group_primkey PRIMARY KEY (event_id, recipient)
)
;

ALTER TABLE wingspan.event_recipient
    OWNER to wingspan;





CREATE TABLE IF NOT EXISTS wingspan.parameter_language
(
    parameter_id character varying COLLATE pg_catalog."default" NOT NULL,
    language character varying COLLATE pg_catalog."default" NOT NULL,
    parameter_name character varying COLLATE pg_catalog."default",
    CONSTRAINT param_lang_pkey PRIMARY KEY (parameter_id, language)
);

ALTER TABLE wingspan.parameter_language
    OWNER to wingspan;





CREATE TABLE IF NOT EXISTS wingspan.tbl_tenant_mode
(
    root_org character varying COLLATE pg_catalog."default" NOT NULL,
    org character varying COLLATE pg_catalog."default" NOT NULL,
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    activated boolean,
    icon_id character varying COLLATE pg_catalog."default",
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    CONSTRAINT tenent_mode_pkey PRIMARY KEY (root_org, org, mode)
);

ALTER TABLE wingspan.tbl_tenant_mode
    OWNER to wingspan;



Create TABLE IF NOT EXISTS wingspan.smtp_config(
	 root_org character varying NOT NULL, 
	 org character varying not null,
	 host character varying NOT NULL, 
	 user_name character varying NOT NULL,
	 password character varying NOT NULL,
	 sign_email boolean default false,
	 port character varying NOT NULL,
	 last_updated_on TIMESTAMP,
	 last_updated_by character varying,
	 sender_id character varying,
     chunk_size int not null,
	 PRIMARY KEY (root_org,org)
);

ALTER TABLE wingspan.smtp_config
    OWNER to wingspan;


CREATE TABLE IF NOT EXISTS wingspan.sns_notification_config
(
    endpoint_platform character varying COLLATE pg_catalog."default" NOT NULL,
    notification_platform character varying COLLATE pg_catalog."default",
    platform_arn character varying COLLATE pg_catalog."default",
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    CONSTRAINT sns_notification_config_pkey PRIMARY KEY (endpoint_platform)
);

ALTER TABLE wingspan.sns_notification_config
    OWNER to wingspan;






CREATE TABLE IF NOT EXISTS wingspan.tenant_mode_language
(
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    mode_name character varying COLLATE pg_catalog."default" NOT NULL,
    language character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT mode_pkey PRIMARY KEY (mode, language)
);

ALTER TABLE wingspan.tenant_mode_language
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.tenant_template_footer
(
    root_org character varying COLLATE pg_catalog."default" NOT NULL,
    org character varying COLLATE pg_catalog."default" NOT NULL,
    template_id character varying COLLATE pg_catalog."default",
    app_email character varying COLLATE pg_catalog."default",
    updated_by character varying COLLATE pg_catalog."default",
    updated_on timestamp without time zone,
    CONSTRAINT tenant_template_footer_pkey PRIMARY KEY (root_org, org)
);

ALTER TABLE wingspan.tenant_template_footer
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.tenant_event_notification
(
    root_org character varying COLLATE pg_catalog."default" NOT NULL,
    org character varying COLLATE pg_catalog."default" NOT NULL,
    event_id character varying COLLATE pg_catalog."default" NOT NULL,
    recipient character varying COLLATE pg_catalog."default" NOT NULL,
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    mode_activated boolean NOT NULL,
    template_id character varying COLLATE pg_catalog."default",
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    receiver_emails character varying COLLATE pg_catalog."default",
    CONSTRAINT tenent_event_pkey PRIMARY KEY (root_org, org, event_id, recipient, mode)
);

ALTER TABLE wingspan.tenant_event_notification
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.tenant_event_template
(
    template_id character varying COLLATE pg_catalog."default" NOT NULL,
    language character varying COLLATE pg_catalog."default" NOT NULL,
    template_subject character varying COLLATE pg_catalog."default" NOT NULL,
    template_text character varying COLLATE pg_catalog."default" NOT NULL,
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    CONSTRAINT tenant_event_template_pkey PRIMARY KEY (template_id, language)
);

ALTER TABLE wingspan.tenant_event_template
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.recipient_description
(
    event_id character varying COLLATE pg_catalog."default" NOT NULL,
    recipient character varying COLLATE pg_catalog."default" NOT NULL,
    language character varying COLLATE pg_catalog."default" NOT NULL,
    recipient_name character varying COLLATE pg_catalog."default" NOT NULL,
    recipient_description character varying COLLATE pg_catalog."default" NOT NULL,
    admin_description character varying COLLATE pg_catalog."default" NOT NULL,
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    CONSTRAINT event_group_description_primkey PRIMARY KEY (event_id, recipient, language)
);

ALTER TABLE wingspan.recipient_description
    OWNER to wingspan;



---Insert commands 

--default configuration for all rootOrg,org


INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('training_register', 'en', 'Register for training', 'Learning', 'Learning', current_timestamp, '', 'When a user registers for a training');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('training_reminder', 'en', 'Reminder for training', 'Learning', 'Learning', current_timestamp, '', 'To remind user for an upcoming training');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('training_complete', 'en', 'Complete training', 'Learning', 'Learning', current_timestamp, '', 'When a user has completed a particular training');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_complete', 'en', 'Complete content', 'Learning', 'Learning', current_timestamp, '', 'When a user completes a content');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('acc_certification_register', 'en', 'Register for certification (ACC)', 'Learning', 'Learning', current_timestamp, '', 'When a user registers for an ACC certification');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('at_desk_certification_register', 'en', 'Register for certification(At-Desk)', 'Learning', 'Learning', current_timestamp, '', 'When a user registers for an at-desk certification');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('at_desk_certification_response', 'en', 'Certification registration response', 'Learning', 'Learning', current_timestamp, '', 'When the proctor approves/rejects request to proctor an at-desk certification');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('complete_certification', 'en', 'Complete a certification', 'Learning', 'Learning', current_timestamp, '', 'When a user completes a certification');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('ask_expert', 'en', 'Ask Question from cohorts', 'Learning', 'Learning', current_timestamp, '', 'When a user asks a query from content owner');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_feedback', 'en', 'Provide content feedback', 'Learning', 'Learning', current_timestamp, '', 'When a user provides feedback to a particular content');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_feedback_response', 'en', 'Response to content feedback', 'Learning', 'Learning', current_timestamp, '', 'When an author responds to a feedback by a user');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('earn_badge', 'en', 'Earn Badge', 'Learning', 'Learning', current_timestamp, '', 'When a user earns a badge on the platform');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('add_contributor', 'en', 'Add contributor', 'Authoring', 'Authoring', current_timestamp, '', 'When an author adds a contributor to his/her content');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('send_content', 'en', 'Author sends content', 'Authoring', 'Authoring', current_timestamp, '', 'When an author sends a content to the next stage');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('approve_content', 'en', 'Approve content', 'Authoring', 'Authoring', current_timestamp, '', 'When the next stage actor approves the content');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('reject_content', 'en', 'Reject content', 'Authoring', 'Authoring', current_timestamp, '', 'When the next stage actor rejects the content');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('publish_content', 'en', 'Publish content', 'Authoring', 'Authoring', current_timestamp, '', 'When a content has been published');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('delegate_content', 'en', 'Delegate content', 'Authoring', 'Authoring', current_timestamp, '', 'When an actor delegates his responsibility to another');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('comment_on_blog', 'en', 'Comment on a blog', 'Social', 'Social', current_timestamp, '', 'When a user comments on a blog');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('follow_user', 'en', 'Follow a user', 'Social', 'Social', current_timestamp, '', 'When a user follows another user');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('share_content', 'en', 'Share content', 'Social', 'Social', current_timestamp, '', 'When a user shares some content with other user(s)');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('share_goal', 'en', 'Share goal', 'Social', 'Social', current_timestamp, '', 'When a user shares a goal with other user(s)');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('share_playlist', 'en', 'Share playlist', 'Social', 'Social', current_timestamp, '', 'When a user shares a playlist with other user(s)');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_expiry_reminder', 'en', 'Content Expiry Reminder', 'Authoring', 'Authoring', current_timestamp, '', 'When some content authored by the author is about to expire');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_expired', 'en', 'Content Expired', 'Authoring', 'Authoring', current_timestamp, '', 'When some content authored by the author expires');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_added_to_kb', 'en', 'Knowledge Board Changed', 'Social', 'Social', current_timestamp, '', 'When a content is added to a knowledge board');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('platform_feedback', 'en', 'Provide Platform Feedback', 'Learning', 'Learning', current_timestamp, '', 'When an user provides platform_feedback');





INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('training_register', 'learner', '#learnerName,#contentTitle,#location,#date,#time,#venue,#startDate,#targetUrl', NULL, current_timestamp, '', NULL, NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('follow_user', 'followedUser', '#followedUserName,#followerName,targetUrl', NULL, current_timestamp, '', NULL, NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('follow_user', 'follower', '#followedUserName,#followerName,#targetUrl', NULL, current_timestamp, '', NULL, NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('training_register', 'manager', '#learnerName,#managerName,#contentTitle,#location,#date,#time,#venue,#startDate,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('training_reminder', 'learner', '#learnerName,#contentTitle,#location,#date,#time,#venue,#startDate,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('training_complete', 'learner', '#learnerName,#contentTitle,#instructorFeedbackUrl,#contentFeedbackUrl,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_complete', 'learner', '#learnerName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('acc_certification_register', 'learner', '#learnerName,#date,#time,#location,#venue,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('at_desk_certification_register', 'learner', '#proctorName,#learnerName,#date,#time,#location,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('at_desk_certification_register', 'proctor', '#proctorName,#learnerName,#date,#time,#location,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('at_desk_certification_response', 'learner', '#learnerName,#proctorName,#action,#date,#time,#location,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('at_desk_certification_response', 'proctor', '#learnerName,#proctorName,#action,#date,#time,#location,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('complete_certification', 'learner', '#learnerName,#score,#scoreType,#date,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('ask_expert', 'learner', '#expertName,#learnerName,#contentType,#contentTitle,#query,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('ask_expert', 'expert', '#expertName,#learnerName,#learnerEmail,#contentType,#contentTitle,#query,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_feedback', 'learner', '#learnerName,#contentType,#contentTitle,#feedback,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_feedback', 'author', '#learnerName,#contentType,#contentTitle,#feedback,#targetUrl', 'app/feedback/my-feedback/author', current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_feedback_response', 'learner', '#learnerName,#authorName,#contentType,#contentTitle,#feedback,#response,#targetUrl', 'app/feedback/my-feedback/user', current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_feedback_response', 'author', '#authorName,#contentType,#contentTitle,#feedback,#response,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('earn_badge', 'learner', '#learnerName,#badgeName,#message,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('add_contributor', 'contributor', '#authorName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('add_contributor', 'author', '#authorName,#contributorName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('send_content', 'author', '#authorName,#nextActor,#nextStage,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('send_content', 'nextActor', '#authorName,#nextActor,#nextStage,#contentType,#contentTitle,#comment,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('approve_content', 'author', '#currentStage,#nextStage,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('approve_content', 'nextActor', '#actorName,#authorName,#nextActor,#nextStage,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('approve_content', 'actor', '#curentActor,#actorName,#nextStage,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('reject_content', 'author', '#currentStage,#contentType,#contentTitle,#comment,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('reject_content', 'actor', '#currentActor,#actorName,#comment,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('publish_content', 'author', '#publisherName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('publish_content', 'publisher', '#publisherName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('delegate_content', 'delegatedActor', '#currentActor,#actorName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('delegate_content', 'actor', '#delegatedActorName,#currentActor,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('comment_on_blog', 'author', '#authorName,#learnerName,#comment,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('comment_on_blog', 'learner', '#learnerName,#comment,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_content', 'sharedWith', '#message,#contentType,#contentTitle,#sharedByName,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_content', 'sharedBy', '#contentType,#contentTitle,#sharedByName,#sharedWithName,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_goal', 'sharedWith', '#message,#contentTitle,#sharedByName,#targetUrl', 'app/goals/me/pending-actions', current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_goal', 'sharedBy', '#contentTitle,#sharedByName,#sharedWithName,#targetUrl', 'app/goals/others', current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_playlist', 'sharedWith', '#message,#contentTitle,#sharedByName,#targetUrl', 'app/playlist/notification', current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_playlist', 'sharedBy', '#contentTitle,#sharedByName,#sharedWithName,#targetUrl', 'app/playlist/notification', current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_expiry_reminder', 'author', '#noOfDays,#targetUrl', 'authoring/home', current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_expired', 'author', '#targetUrl', 'authoring/home', current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_added_to_kb', 'follower', '#contentTitle,#targetUrl', 'app/knowledge-board/{lexId}', current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('platform_feedback', 'learner', '#feedback', NULL, current_timestamp, 'admin@eagle.com', 'Information', NULL);



INSERT INTO wingspan.parameter_language (parameter_id, language, parameter_name) VALUES ('reminder_threshold', 'en', 'No. of days before content expires');





INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('training_register', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('training_register', 'manager', 'en', 'Manager', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('training_reminder', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('training_complete', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_complete', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('acc_certification_register', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('at_desk_certification_register', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('at_desk_certification_register', 'proctor', 'en', 'Proctor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('at_desk_certification_response', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('at_desk_certification_response', 'proctor', 'en', 'Proctor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('complete_certification', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('ask_expert', 'expert', 'en', 'Expert', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('ask_expert', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_feedback', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_feedback', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_feedback_response', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_feedback_response', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('earn_badge', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('add_contributor', 'contributor', 'en', 'Contributor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('add_contributor', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('send_content', 'nextActor', 'en', 'Next Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('send_content', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('approve_content', 'nextActor', 'en', 'Next Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('approve_content', 'actor', 'en', 'Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('approve_content', 'author', 'en', 'Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('reject_content', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('reject_content', 'actor', 'en', 'Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('publish_content', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('publish_content', 'publisher', 'en', 'Publisher', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('delegate_content', 'delegatedActor', 'en', 'Delegated Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('delegate_content', 'actor', 'en', 'Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('comment_on_blog', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('comment_on_blog', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('follow_user', 'followedUser', 'en', 'Followed User', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('follow_user', 'follower', 'en', 'Follower', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_content', 'sharedWith', 'en', 'Shared With', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_content', 'sharedBy', 'en', 'Shared By', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_goal', 'sharedWith', 'en', 'Shared With', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_goal', 'sharedBy', 'en', 'Shared By', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_playlist', 'sharedWith', 'en', 'Shared With', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_playlist', 'sharedBy', 'en', 'Shared By', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_expiry_reminder', 'author', 'en', 'Author', 'Some description', 'Some Description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_expired', 'author', 'en', 'Author', 'Some description', 'Some Description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_added_to_kb', 'follower', 'en', 'Follower', 'Some description', 'Some Description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('platform_feedback', 'learner', 'en', 'Learner', 'Platform Learner', 'Some description', current_timestamp, '');







INSERT INTO wingspan.tbl_tenant_mode (root_org, org, mode, activated, icon_id, updated_on, updated_by) VALUES ('default', 'default', 'email', true, '8910', current_timestamp, '');
INSERT INTO wingspan.tbl_tenant_mode (root_org, org, mode, activated, icon_id, updated_on, updated_by) VALUES ('default', 'default', 'push', true, '4567', current_timestamp, '');
INSERT INTO wingspan.tbl_tenant_mode (root_org, org, mode, activated, icon_id, updated_on, updated_by) VALUES ('default', 'default', 'sms', true, '1234', current_timestamp, '');






INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('sms', 'Sms', 'en');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('sms', 'Sms', 'de');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('sms', 'Sms', 'fr');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('push', 'Push', 'en');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('push', 'Push', 'de');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('push', 'Push', 'fr');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('email', 'Email', 'en');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('email', 'Email', 'de');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('email', 'Email', 'fr');




INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_complete', 'learner', 'email', true, 'xxe1db08-dc39-48aa-a3db-08dc3958zxcm', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_complete', 'learner', 'inApp', true, 'xxe1db08-dc39-48da-a3db-08dc3958zxco', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'expert', 'email', true, 'zxc1db08-dc39-48ea-a3dc-08dc3958zxde', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'expert', 'inApp', true, 'zxc1db08-dc39-48ea-a3dc-08dc3958zxdg', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'learner', 'email', true, 'zxc1db08-dc39-48ea-b3db-08dc3958zxdi', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'learner', 'inApp', true, 'zxc1db08-dc39-48ea-b3db-08dc3958zxl', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'author', 'email', true, 'zxc1db08-dc39-48ea-a3db-09dc3958zxdl', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'author', 'inApp', true, 'zxc1db08-dc39-48ea-a3db-09dc3958zxjl', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'learner', 'email', true, 'zxc1cc08-dc39-48ea-a5db-08dc3958zxdo', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'learner', 'inApp', true, 'zxc1cc08-dc39-48ea-a8db-08dc3958zxdq', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'learner', 'email', true, 'zzc1db08-ac39-48ea-a3db-08dc3958zxdr', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'learner', 'inApp', true, 'zzc1db08-dc39-48ea-a3db-08dc3958zxdt', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'author', 'email', true, 'zxc1dbf8-dc39-48ea-a3db-08dc3958zxdu', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'author', 'inApp', true, 'zxc1dbf8-dc39-48ea-a3db-08dc3958zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'earn_badge', 'learner', 'email', true, 'zxa1db08-dc39-48ea-a3db-09dc3958zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'earn_badge', 'learner', 'inApp', true, 'zxa1db08-dc39-48ea-a3db-09dc3958zxea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'contributor', 'email', true, 'authdb08-ac39-48ea-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'contributor', 'inApp', true, 'authdb08-ac39-48ea-a3db-08dc3969zxda', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'author', 'email', true, 'authdb08-ac39-48ea-a3db-08dc3969zxex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'author', 'inApp', true, 'authdb08-ac39-48ea-a3db-08dc3969zxea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'nextActor', 'email', true, 'authdb08-ad49-48ea-a3db-08dc3969zaex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'nextActor', 'inApp', true, 'authdb08-ad49-48ea-a3db-08dc3969zaea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'author', 'email', true, 'authdb08-ad49-19ea-a3db-08dc3968zaex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'author', 'inApp', true, 'authdb08-ad49-19ea-a3db-08dc3869zaea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'nextActor', 'email', true, 'authdb08-ad49-21ea-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'nextActor', 'inApp', true, 'authdb08-ad49-21ea-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'author', 'email', true, 'authdb18-bd59-21ea-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'author', 'inApp', true, 'authdb18-bd59-21ea-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'actor', 'email', true, 'authdb18-cd59-2wea-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'actor', 'inApp', true, 'authdb18-cd59-2wea-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'author', 'email', true, 'authdb18-cd69-3wea-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'author', 'inApp', true, 'authdb18-cd69-3wea-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'actor', 'email', true, 'authdb18-ed69-3wfa-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'actor', 'inApp', true, 'authdb18-ed69-3wfa-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'author', 'email', true, 'authdb22-fd69-3wfa-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'author', 'inApp', true, 'authdb22-fd69-3wfa-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'publisher', 'email', true, 'authdb23-fd61-3wfa-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'publisher', 'inApp', true, 'authdb23-fd61-3wfa-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'delegatedActor', 'email', true, 'authdb24-fd63-3wfa-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'delegatedActor', 'inApp', true, 'authdb24-fd63-3wfa-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'actor', 'email', true, 'authdb25-fd65-3wfa-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'actor', 'inApp', true, 'authdb25-fd65-3wfa-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedWith', 'email', true, 'zxc2eb08-dc39-49ea-a3db-08dc396azxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedWith', 'inApp', true, 'zxc1db08-dc39-51ea-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedBy', 'email', true, 'zxc1db08-dc39-48ea-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedBy', 'inApp', true, 'zxc1db08-dc39-51fa-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedWith', 'email', true, 'zxc2eb08-eac9-91fa-a3db-08dc396azxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedWith', 'inApp', true, 'zxc1db08-eac9-91fa-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedBy', 'email', true, 'zxc2eb08-eac9-91fa-p3f4-08dc396azxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedBy', 'inApp', true, 'zxc1db08-eac9-91fa-p3f4-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedWith', 'email', true, 'b5hteb08-eac9-91fm-a3db-08dc396azxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedWith', 'push', true, 'b5hteb08-eac9-91fm-a3db-08dc396azvdy', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedWith', 'inApp', true, 'b5hteb08-eac9-91fm-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedBy', 'email', true, 'b5hteb08-eac9-91fa-p5gt-08dc396azxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedBy', 'inApp', true, 'b5hteb08-eac9-91fa-p5gt-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_expiry_reminder', 'author', 'inApp', true, '716e730a-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_expired', 'author', 'inApp', true, '716e77a6-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_added_to_kb', 'follower', 'inApp', true, '716e7918-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_expiry_reminder', 'author', 'email', true, '716e7a58-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_expired', 'author', 'email', true, '716e7b8e-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_added_to_kb', 'follower', 'email', true, '716e7cc4-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'platform_feedback', 'learner', 'email', true, '31bfc794-3b45-11ea-b77f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedBy', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedWith', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedBy', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedBy', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'manager', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'manager', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'manager', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'manager', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_reminder', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_reminder', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_reminder', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_reminder', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_complete', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_complete', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_complete', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_complete', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_complete', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_complete', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'acc_certification_register', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'acc_certification_register', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'acc_certification_register', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'acc_certification_register', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'proctor', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'proctor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'proctor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'proctor', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'proctor', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'proctor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'proctor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'proctor', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'complete_certification', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'complete_certification', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'complete_certification', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'complete_certification', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'expert', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'expert', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'earn_badge', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'earn_badge', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'contributor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'contributor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'nextActor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'nextActor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'nextActor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'nextActor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'actor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'actor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'actor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'actor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'publisher', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'publisher', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'delegatedActor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'delegatedActor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'actor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'actor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'author', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'author', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'followedUser', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'followedUser', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'followedUser', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'followedUser', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'follower', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'follower', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'follower', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'follower', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedWith', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedWith', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedBy', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedBy', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedWith', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedWith', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedBy', 'push', true, NULL, current_timestamp, '', NULL);



--- add default push template for share_content sharedBy
update wingspan.tenant_event_notification set template_id = '1681d80c-7f95-11ea-bc55-0242ac130003' where root_org = 'default' and event_id = 'share_content' and recipient = 'sharedBy' and mode = 'push';

INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('1681d80c-7f95-11ea-bc55-0242ac130003', 'en', 'Content Shared', 'You have shared the #contentType #contentTitle with #sharedWithCount user(s).', current_timestamp, '');



---Drop commands
DROP TABLE IF EXISTS wingspan.event_group;

DROP TABLE IF EXISTS wingspan.event_parameters;

DROP TABLE IF EXISTS wingspan.event_recipient;

DROP TABLE IF EXISTS wingspan.parameter_language;

DROP TABLE IF EXISTS wingspan.tbl_tenant_mode;

DROP TABLE IF EXISTS wingspan.smtp_config;

DROP TABLE IF EXISTS wingspan.sns_notification_config;

DROP TABLE IF EXISTS wingspan.tenant_mode_language;

DROP TABLE IF EXISTS wingspan.tenant_template_footer;

DROP TABLE IF EXISTS wingspan.tenant_event_notification;

DROP TABLE IF EXISTS wingspan.tenant_event_template;

DROP TABLE IF EXISTS wingspan.recipient_description;








CREATE TABLE IF NOT EXISTS wingspan.event_group
(
    event_id character varying COLLATE pg_catalog."default" NOT NULL,
    language character varying COLLATE pg_catalog."default" NOT NULL,
    event_name character varying COLLATE pg_catalog."default" NOT NULL,
    group_id character varying COLLATE pg_catalog."default" NOT NULL,
    group_name character varying COLLATE pg_catalog."default" NOT NULL,
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    event_description character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT event_group_prim_key PRIMARY KEY (event_id, language)
);


ALTER TABLE wingspan.event_group
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.event_parameters
(
    root_org character varying COLLATE pg_catalog."default" NOT NULL,
    org character varying COLLATE pg_catalog."default" NOT NULL,
    event_id character varying COLLATE pg_catalog."default" NOT NULL,
    parameter_id character varying COLLATE pg_catalog."default",
    value character varying COLLATE pg_catalog."default",
    CONSTRAINT event_param_pkey PRIMARY KEY (root_org, org, event_id)
);

ALTER TABLE wingspan.event_parameters
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.event_recipient
(
    event_id character varying COLLATE pg_catalog."default" NOT NULL,
    recipient character varying COLLATE pg_catalog."default" NOT NULL,
    tag character varying COLLATE pg_catalog."default" NOT NULL,
    target_url character varying COLLATE pg_catalog."default",
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    classification character varying COLLATE pg_catalog."default",
    user_roles character varying COLLATE pg_catalog."default",
    CONSTRAINT event_group_primkey PRIMARY KEY (event_id, recipient)
)
;

ALTER TABLE wingspan.event_recipient
    OWNER to wingspan;





CREATE TABLE IF NOT EXISTS wingspan.parameter_language
(
    parameter_id character varying COLLATE pg_catalog."default" NOT NULL,
    language character varying COLLATE pg_catalog."default" NOT NULL,
    parameter_name character varying COLLATE pg_catalog."default",
    CONSTRAINT param_lang_pkey PRIMARY KEY (parameter_id, language)
);

ALTER TABLE wingspan.parameter_language
    OWNER to wingspan;





CREATE TABLE IF NOT EXISTS wingspan.tbl_tenant_mode
(
    root_org character varying COLLATE pg_catalog."default" NOT NULL,
    org character varying COLLATE pg_catalog."default" NOT NULL,
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    activated boolean,
    icon_id character varying COLLATE pg_catalog."default",
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    CONSTRAINT tenent_mode_pkey PRIMARY KEY (root_org, org, mode)
);

ALTER TABLE wingspan.tbl_tenant_mode
    OWNER to wingspan;



Create TABLE IF NOT EXISTS wingspan.smtp_config(
	 root_org character varying NOT NULL, 
	 org character varying not null,
	 host character varying NOT NULL, 
	 user_name character varying NOT NULL,
	 password character varying NOT NULL,
	 sign_email boolean default false,
	 port character varying NOT NULL,
	 last_updated_on TIMESTAMP,
	 last_updated_by character varying,
	 sender_id character varying,
     chunk_size int not null,
	 PRIMARY KEY (root_org,org)
);

ALTER TABLE wingspan.smtp_config
    OWNER to wingspan;


CREATE TABLE IF NOT EXISTS wingspan.sns_notification_config
(
    endpoint_platform character varying COLLATE pg_catalog."default" NOT NULL,
    notification_platform character varying COLLATE pg_catalog."default",
    platform_arn character varying COLLATE pg_catalog."default",
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    CONSTRAINT sns_notification_config_pkey PRIMARY KEY (endpoint_platform)
);

ALTER TABLE wingspan.sns_notification_config
    OWNER to wingspan;






CREATE TABLE IF NOT EXISTS wingspan.tenant_mode_language
(
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    mode_name character varying COLLATE pg_catalog."default" NOT NULL,
    language character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT mode_pkey PRIMARY KEY (mode, language)
);

ALTER TABLE wingspan.tenant_mode_language
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.tenant_template_footer
(
    root_org character varying COLLATE pg_catalog."default" NOT NULL,
    org character varying COLLATE pg_catalog."default" NOT NULL,
    template_id character varying COLLATE pg_catalog."default",
    app_email character varying COLLATE pg_catalog."default",
    updated_by character varying COLLATE pg_catalog."default",
    updated_on timestamp without time zone,
    CONSTRAINT tenant_template_footer_pkey PRIMARY KEY (root_org, org)
);

ALTER TABLE wingspan.tenant_template_footer
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.tenant_event_notification
(
    root_org character varying COLLATE pg_catalog."default" NOT NULL,
    org character varying COLLATE pg_catalog."default" NOT NULL,
    event_id character varying COLLATE pg_catalog."default" NOT NULL,
    recipient character varying COLLATE pg_catalog."default" NOT NULL,
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    mode_activated boolean NOT NULL,
    template_id character varying COLLATE pg_catalog."default",
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    receiver_emails character varying COLLATE pg_catalog."default",
    CONSTRAINT tenent_event_pkey PRIMARY KEY (root_org, org, event_id, recipient, mode)
);

ALTER TABLE wingspan.tenant_event_notification
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.tenant_event_template
(
    template_id character varying COLLATE pg_catalog."default" NOT NULL,
    language character varying COLLATE pg_catalog."default" NOT NULL,
    template_subject character varying COLLATE pg_catalog."default" NOT NULL,
    template_text character varying COLLATE pg_catalog."default" NOT NULL,
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    CONSTRAINT tenant_event_template_pkey PRIMARY KEY (template_id, language)
);

ALTER TABLE wingspan.tenant_event_template
    OWNER to wingspan;




CREATE TABLE IF NOT EXISTS wingspan.recipient_description
(
    event_id character varying COLLATE pg_catalog."default" NOT NULL,
    recipient character varying COLLATE pg_catalog."default" NOT NULL,
    language character varying COLLATE pg_catalog."default" NOT NULL,
    recipient_name character varying COLLATE pg_catalog."default" NOT NULL,
    recipient_description character varying COLLATE pg_catalog."default" NOT NULL,
    admin_description character varying COLLATE pg_catalog."default" NOT NULL,
    updated_on timestamp without time zone,
    updated_by character varying COLLATE pg_catalog."default",
    CONSTRAINT event_group_description_primkey PRIMARY KEY (event_id, recipient, language)
);

ALTER TABLE wingspan.recipient_description
    OWNER to wingspan;



---Insert commands 

--default configuration for all rootOrg,org


INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('training_register', 'en', 'Register for training', 'Learning', 'Learning', current_timestamp, '', 'When a user registers for a training');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('training_reminder', 'en', 'Reminder for training', 'Learning', 'Learning', current_timestamp, '', 'To remind user for an upcoming training');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('training_complete', 'en', 'Complete training', 'Learning', 'Learning', current_timestamp, '', 'When a user has completed a particular training');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_complete', 'en', 'Complete content', 'Learning', 'Learning', current_timestamp, '', 'When a user completes a content');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('acc_certification_register', 'en', 'Register for certification (ACC)', 'Learning', 'Learning', current_timestamp, '', 'When a user registers for an ACC certification');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('at_desk_certification_register', 'en', 'Register for certification(At-Desk)', 'Learning', 'Learning', current_timestamp, '', 'When a user registers for an at-desk certification');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('at_desk_certification_response', 'en', 'Certification registration response', 'Learning', 'Learning', current_timestamp, '', 'When the proctor approves/rejects request to proctor an at-desk certification');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('complete_certification', 'en', 'Complete a certification', 'Learning', 'Learning', current_timestamp, '', 'When a user completes a certification');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('ask_expert', 'en', 'Ask Question from cohorts', 'Learning', 'Learning', current_timestamp, '', 'When a user asks a query from content owner');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_feedback', 'en', 'Provide content feedback', 'Learning', 'Learning', current_timestamp, '', 'When a user provides feedback to a particular content');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_feedback_response', 'en', 'Response to content feedback', 'Learning', 'Learning', current_timestamp, '', 'When an author responds to a feedback by a user');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('earn_badge', 'en', 'Earn Badge', 'Learning', 'Learning', current_timestamp, '', 'When a user earns a badge on the platform');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('add_contributor', 'en', 'Add contributor', 'Authoring', 'Authoring', current_timestamp, '', 'When an author adds a contributor to his/her content');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('send_content', 'en', 'Author sends content', 'Authoring', 'Authoring', current_timestamp, '', 'When an author sends a content to the next stage');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('approve_content', 'en', 'Approve content', 'Authoring', 'Authoring', current_timestamp, '', 'When the next stage actor approves the content');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('reject_content', 'en', 'Reject content', 'Authoring', 'Authoring', current_timestamp, '', 'When the next stage actor rejects the content');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('publish_content', 'en', 'Publish content', 'Authoring', 'Authoring', current_timestamp, '', 'When a content has been published');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('delegate_content', 'en', 'Delegate content', 'Authoring', 'Authoring', current_timestamp, '', 'When an actor delegates his responsibility to another');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('comment_on_blog', 'en', 'Comment on a blog', 'Social', 'Social', current_timestamp, '', 'When a user comments on a blog');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('follow_user', 'en', 'Follow a user', 'Social', 'Social', current_timestamp, '', 'When a user follows another user');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('share_content', 'en', 'Share content', 'Social', 'Social', current_timestamp, '', 'When a user shares some content with other user(s)');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('share_goal', 'en', 'Share goal', 'Social', 'Social', current_timestamp, '', 'When a user shares a goal with other user(s)');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('share_playlist', 'en', 'Share playlist', 'Social', 'Social', current_timestamp, '', 'When a user shares a playlist with other user(s)');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_expiry_reminder', 'en', 'Content Expiry Reminder', 'Authoring', 'Authoring', current_timestamp, '', 'When some content authored by the author is about to expire');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_expired', 'en', 'Content Expired', 'Authoring', 'Authoring', current_timestamp, '', 'When some content authored by the author expires');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('content_added_to_kb', 'en', 'Knowledge Board Changed', 'Social', 'Social', current_timestamp, '', 'When a content is added to a knowledge board');
INSERT INTO wingspan.event_group (event_id, language, event_name, group_id, group_name, updated_on, updated_by, event_description) VALUES ('platform_feedback', 'en', 'Provide Platform Feedback', 'Learning', 'Learning', current_timestamp, '', 'When an user provides platform_feedback');





INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('training_register', 'learner', '#learnerName,#contentTitle,#location,#date,#time,#venue,#startDate,#targetUrl', NULL, current_timestamp, '', NULL, NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('follow_user', 'followedUser', '#followedUserName,#followerName,targetUrl', NULL, current_timestamp, '', NULL, NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('follow_user', 'follower', '#followedUserName,#followerName,#targetUrl', NULL, current_timestamp, '', NULL, NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('training_register', 'manager', '#learnerName,#managerName,#contentTitle,#location,#date,#time,#venue,#startDate,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('training_reminder', 'learner', '#learnerName,#contentTitle,#location,#date,#time,#venue,#startDate,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('training_complete', 'learner', '#learnerName,#contentTitle,#instructorFeedbackUrl,#contentFeedbackUrl,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_complete', 'learner', '#learnerName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('acc_certification_register', 'learner', '#learnerName,#date,#time,#location,#venue,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('at_desk_certification_register', 'learner', '#proctorName,#learnerName,#date,#time,#location,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('at_desk_certification_register', 'proctor', '#proctorName,#learnerName,#date,#time,#location,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('at_desk_certification_response', 'learner', '#learnerName,#proctorName,#action,#date,#time,#location,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('at_desk_certification_response', 'proctor', '#learnerName,#proctorName,#action,#date,#time,#location,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('complete_certification', 'learner', '#learnerName,#score,#scoreType,#date,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('ask_expert', 'learner', '#expertName,#learnerName,#contentType,#contentTitle,#query,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('ask_expert', 'expert', '#expertName,#learnerName,#learnerEmail,#contentType,#contentTitle,#query,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_feedback', 'learner', '#learnerName,#contentType,#contentTitle,#feedback,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_feedback', 'author', '#learnerName,#contentType,#contentTitle,#feedback,#targetUrl', 'app/feedback/my-feedback/author', current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_feedback_response', 'learner', '#learnerName,#authorName,#contentType,#contentTitle,#feedback,#response,#targetUrl', 'app/feedback/my-feedback/user', current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_feedback_response', 'author', '#authorName,#contentType,#contentTitle,#feedback,#response,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('earn_badge', 'learner', '#learnerName,#badgeName,#message,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('add_contributor', 'contributor', '#authorName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('add_contributor', 'author', '#authorName,#contributorName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('send_content', 'author', '#authorName,#nextActor,#nextStage,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('send_content', 'nextActor', '#authorName,#nextActor,#nextStage,#contentType,#contentTitle,#comment,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('approve_content', 'author', '#currentStage,#nextStage,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('approve_content', 'nextActor', '#actorName,#authorName,#nextActor,#nextStage,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('approve_content', 'actor', '#curentActor,#actorName,#nextStage,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('reject_content', 'author', '#currentStage,#contentType,#contentTitle,#comment,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('reject_content', 'actor', '#currentActor,#actorName,#comment,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('publish_content', 'author', '#publisherName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('publish_content', 'publisher', '#publisherName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('delegate_content', 'delegatedActor', '#currentActor,#actorName,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('delegate_content', 'actor', '#delegatedActorName,#currentActor,#contentType,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('comment_on_blog', 'author', '#authorName,#learnerName,#comment,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('comment_on_blog', 'learner', '#learnerName,#comment,#contentTitle,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_content', 'sharedWith', '#message,#contentType,#contentTitle,#sharedByName,#targetUrl', NULL, current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_content', 'sharedBy', '#contentType,#contentTitle,#sharedByName,#sharedWithName,#targetUrl', NULL, current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_goal', 'sharedWith', '#message,#contentTitle,#sharedByName,#targetUrl', 'app/goals/me/pending-actions', current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_goal', 'sharedBy', '#contentTitle,#sharedByName,#sharedWithName,#targetUrl', 'app/goals/others', current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_playlist', 'sharedWith', '#message,#contentTitle,#sharedByName,#targetUrl', 'app/playlist/notification', current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('share_playlist', 'sharedBy', '#contentTitle,#sharedByName,#sharedWithName,#targetUrl', 'app/playlist/notification', current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_expiry_reminder', 'author', '#noOfDays,#targetUrl', 'authoring/home', current_timestamp, '', 'Action', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_expired', 'author', '#targetUrl', 'authoring/home', current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('content_added_to_kb', 'follower', '#contentTitle,#targetUrl', 'app/knowledge-board/{lexId}', current_timestamp, '', 'Information', NULL);
INSERT INTO wingspan.event_recipient (event_id, recipient, tag, target_url, updated_on, updated_by, classification, user_roles) VALUES ('platform_feedback', 'learner', '#feedback', NULL, current_timestamp, 'admin@eagle.com', 'Information', NULL);



INSERT INTO wingspan.parameter_language (parameter_id, language, parameter_name) VALUES ('reminder_threshold', 'en', 'No. of days before content expires');





INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('training_register', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('training_register', 'manager', 'en', 'Manager', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('training_reminder', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('training_complete', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_complete', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('acc_certification_register', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('at_desk_certification_register', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('at_desk_certification_register', 'proctor', 'en', 'Proctor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('at_desk_certification_response', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('at_desk_certification_response', 'proctor', 'en', 'Proctor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('complete_certification', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('ask_expert', 'expert', 'en', 'Expert', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('ask_expert', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_feedback', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_feedback', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_feedback_response', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_feedback_response', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('earn_badge', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('add_contributor', 'contributor', 'en', 'Contributor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('add_contributor', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('send_content', 'nextActor', 'en', 'Next Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('send_content', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('approve_content', 'nextActor', 'en', 'Next Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('approve_content', 'actor', 'en', 'Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('approve_content', 'author', 'en', 'Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('reject_content', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('reject_content', 'actor', 'en', 'Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('publish_content', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('publish_content', 'publisher', 'en', 'Publisher', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('delegate_content', 'delegatedActor', 'en', 'Delegated Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('delegate_content', 'actor', 'en', 'Actor', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('comment_on_blog', 'author', 'en', 'Author', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('comment_on_blog', 'learner', 'en', 'Learner', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('follow_user', 'followedUser', 'en', 'Followed User', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('follow_user', 'follower', 'en', 'Follower', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_content', 'sharedWith', 'en', 'Shared With', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_content', 'sharedBy', 'en', 'Shared By', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_goal', 'sharedWith', 'en', 'Shared With', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_goal', 'sharedBy', 'en', 'Shared By', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_playlist', 'sharedWith', 'en', 'Shared With', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('share_playlist', 'sharedBy', 'en', 'Shared By', 'Some description', 'Some description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_expiry_reminder', 'author', 'en', 'Author', 'Some description', 'Some Description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_expired', 'author', 'en', 'Author', 'Some description', 'Some Description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('content_added_to_kb', 'follower', 'en', 'Follower', 'Some description', 'Some Description', current_timestamp, '');
INSERT INTO wingspan.recipient_description (event_id, recipient, language, recipient_name, recipient_description, admin_description, updated_on, updated_by) VALUES ('platform_feedback', 'learner', 'en', 'Learner', 'Platform Learner', 'Some description', current_timestamp, '');







INSERT INTO wingspan.tbl_tenant_mode (root_org, org, mode, activated, icon_id, updated_on, updated_by) VALUES ('default', 'default', 'email', true, '8910', current_timestamp, '');
INSERT INTO wingspan.tbl_tenant_mode (root_org, org, mode, activated, icon_id, updated_on, updated_by) VALUES ('default', 'default', 'push', true, '4567', current_timestamp, '');
INSERT INTO wingspan.tbl_tenant_mode (root_org, org, mode, activated, icon_id, updated_on, updated_by) VALUES ('default', 'default', 'sms', true, '1234', current_timestamp, '');






INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('sms', 'Sms', 'en');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('sms', 'Sms', 'de');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('sms', 'Sms', 'fr');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('push', 'Push', 'en');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('push', 'Push', 'de');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('push', 'Push', 'fr');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('email', 'Email', 'en');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('email', 'Email', 'de');
INSERT INTO wingspan.tenant_mode_language (mode, mode_name, language) VALUES ('email', 'Email', 'fr');





INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_complete', 'learner', 'email', true, 'xxe1db08-dc39-48aa-a3db-08dc3958zxcm', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_complete', 'learner', 'inApp', true, 'xxe1db08-dc39-48da-a3db-08dc3958zxco', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'expert', 'email', true, 'zxc1db08-dc39-48ea-a3dc-08dc3958zxde', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'expert', 'inApp', true, 'zxc1db08-dc39-48ea-a3dc-08dc3958zxdg', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'learner', 'email', true, 'zxc1db08-dc39-48ea-b3db-08dc3958zxdi', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'learner', 'inApp', true, 'zxc1db08-dc39-48ea-b3db-08dc3958zxl', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'author', 'email', true, 'zxc1db08-dc39-48ea-a3db-09dc3958zxdl', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'author', 'inApp', true, 'zxc1db08-dc39-48ea-a3db-09dc3958zxjl', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'learner', 'email', true, 'zxc1cc08-dc39-48ea-a5db-08dc3958zxdo', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'learner', 'inApp', true, 'zxc1cc08-dc39-48ea-a8db-08dc3958zxdq', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'learner', 'email', true, 'zzc1db08-ac39-48ea-a3db-08dc3958zxdr', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'learner', 'inApp', true, 'zzc1db08-dc39-48ea-a3db-08dc3958zxdt', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'author', 'email', true, 'zxc1dbf8-dc39-48ea-a3db-08dc3958zxdu', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'author', 'inApp', true, 'zxc1dbf8-dc39-48ea-a3db-08dc3958zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'earn_badge', 'learner', 'email', true, 'zxa1db08-dc39-48ea-a3db-09dc3958zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'earn_badge', 'learner', 'inApp', true, 'zxa1db08-dc39-48ea-a3db-09dc3958zxea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'contributor', 'email', true, 'authdb08-ac39-48ea-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'contributor', 'inApp', true, 'authdb08-ac39-48ea-a3db-08dc3969zxda', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'author', 'email', true, 'authdb08-ac39-48ea-a3db-08dc3969zxex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'author', 'inApp', true, 'authdb08-ac39-48ea-a3db-08dc3969zxea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'nextActor', 'email', true, 'authdb08-ad49-48ea-a3db-08dc3969zaex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'nextActor', 'inApp', true, 'authdb08-ad49-48ea-a3db-08dc3969zaea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'author', 'email', true, 'authdb08-ad49-19ea-a3db-08dc3968zaex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'author', 'inApp', true, 'authdb08-ad49-19ea-a3db-08dc3869zaea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'nextActor', 'email', true, 'authdb08-ad49-21ea-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'nextActor', 'inApp', true, 'authdb08-ad49-21ea-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'author', 'email', true, 'authdb18-bd59-21ea-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'author', 'inApp', true, 'authdb18-bd59-21ea-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'actor', 'email', true, 'authdb18-cd59-2wea-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'actor', 'inApp', true, 'authdb18-cd59-2wea-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'author', 'email', true, 'authdb18-cd69-3wea-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'author', 'inApp', true, 'authdb18-cd69-3wea-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'actor', 'email', true, 'authdb18-ed69-3wfa-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'actor', 'inApp', true, 'authdb18-ed69-3wfa-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'author', 'email', true, 'authdb22-fd69-3wfa-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'author', 'inApp', true, 'authdb22-fd69-3wfa-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'publisher', 'email', true, 'authdb23-fd61-3wfa-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'publisher', 'inApp', true, 'authdb23-fd61-3wfa-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'delegatedActor', 'email', true, 'authdb24-fd63-3wfa-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'delegatedActor', 'inApp', true, 'authdb24-fd63-3wfa-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'actor', 'email', true, 'authdb25-fd65-3wfa-a3db-08dc3968zbex', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'actor', 'inApp', true, 'authdb25-fd65-3wfa-a3db-08dc3869zbea', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedWith', 'email', true, 'zxc2eb08-dc39-49ea-a3db-08dc396azxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedWith', 'inApp', true, 'zxc1db08-dc39-51ea-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedBy', 'email', true, 'zxc1db08-dc39-48ea-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedBy', 'inApp', true, 'zxc1db08-dc39-51fa-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedWith', 'email', true, 'zxc2eb08-eac9-91fa-a3db-08dc396azxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedWith', 'inApp', true, 'zxc1db08-eac9-91fa-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedBy', 'email', true, 'zxc2eb08-eac9-91fa-p3f4-08dc396azxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedBy', 'inApp', true, 'zxc1db08-eac9-91fa-p3f4-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedWith', 'email', true, 'b5hteb08-eac9-91fm-a3db-08dc396azxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedWith', 'push', true, 'b5hteb08-eac9-91fm-a3db-08dc396azvdy', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedWith', 'inApp', true, 'b5hteb08-eac9-91fm-a3db-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedBy', 'email', true, 'b5hteb08-eac9-91fa-p5gt-08dc396azxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedBy', 'inApp', true, 'b5hteb08-eac9-91fa-p5gt-08dc3969zxdx', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_expiry_reminder', 'author', 'inApp', true, '716e730a-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_expired', 'author', 'inApp', true, '716e77a6-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_added_to_kb', 'follower', 'inApp', true, '716e7918-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_expiry_reminder', 'author', 'email', true, '716e7a58-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_expired', 'author', 'email', true, '716e7b8e-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_added_to_kb', 'follower', 'email', true, '716e7cc4-1d8a-11ea-978f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'platform_feedback', 'learner', 'email', true, '31bfc794-3b45-11ea-b77f-2e728ce88125', current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedBy', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedWith', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedBy', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_playlist', 'sharedBy', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'manager', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'manager', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'manager', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_register', 'manager', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_reminder', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_reminder', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_reminder', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_reminder', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_complete', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_complete', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_complete', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'training_complete', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_complete', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_complete', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'acc_certification_register', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'acc_certification_register', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'acc_certification_register', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'acc_certification_register', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'proctor', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'proctor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'proctor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'proctor', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_register', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'proctor', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'proctor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'proctor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'at_desk_certification_response', 'proctor', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'complete_certification', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'complete_certification', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'complete_certification', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'complete_certification', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'expert', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'expert', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'ask_expert', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'content_feedback_response', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'earn_badge', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'earn_badge', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'contributor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'contributor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'add_contributor', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'nextActor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'nextActor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'send_content', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'nextActor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'nextActor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'actor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'approve_content', 'actor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'actor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'reject_content', 'actor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'publisher', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'publish_content', 'publisher', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'delegatedActor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'delegatedActor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'actor', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'delegate_content', 'actor', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'author', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'author', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'author', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'author', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'learner', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'learner', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'learner', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'comment_on_blog', 'learner', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'followedUser', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'followedUser', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'followedUser', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'followedUser', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'follower', 'email', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'follower', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'follower', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'follow_user', 'follower', 'inApp', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedWith', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedWith', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedBy', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_content', 'sharedBy', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedWith', 'push', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedWith', 'sms', true, NULL, current_timestamp, '', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('default', 'default', 'share_goal', 'sharedBy', 'push', true, NULL, current_timestamp, '', NULL);



--- add default push template for share_content sharedBy
update wingspan.tenant_event_notification set template_id = '1681d80c-7f95-11ea-bc55-0242ac130003' where root_org = 'default' and event_id = 'share_content' and recipient = 'sharedBy' and mode = 'push';

INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('1681d80c-7f95-11ea-bc55-0242ac130003', 'en', 'Content Shared', 'You have shared the #contentType #contentTitle with #sharedWithCount user(s).', current_timestamp, '');


----add training register default for both recipients
--learner
update wingspan.tenant_event_notification set template_id = 'fd728e6a-7fa8-11ea-bc55-0242ac130003' where root_org = 'default' and event_id = 'training_register' and recipient = 'learner' and mode = 'email';

INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fd728e6a-7fa8-11ea-bc55-0242ac130003', 'en', 'Your team member has registered for instructor led training - #contentTitle', '<p>Hi #managerName,</p>
<p>Please take a few minutes and consider auto-approved training registration request for user #learnerName.</p>
<p>Course Name&nbsp;&nbsp;&nbsp;&nbsp; :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; #contentTitle</p>
<p>Location&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; #location (City, Country)</p>
<table>
<tbody>
<tr>
<td width="114">
<p>Date</p>
</td>
<td width="150">
<p>Time</p>
</td>
<td width="360">
<p>Venue</p>
</td>
</tr>
<tr>
<td width="114">
<p>#date</p>
</td>
<td width="150">
<p>#time</p>
</td>
<td width="360">
<p>#venue</p>
</td>
</tr>
</tbody>
</table>
<p><br /> You can forward or reject this request within 3 days.</p><p>Have a great day! <br /> <br /> Thanks, <br /> Your Wingspan Team</p>
<p>Too many notifications? Click <a href="#appUrl/app/profile/settings?tab=notifications">here</a> to unsubscribe.</p>', current_timestamp, '');


--manager
update wingspan.tenant_event_notification set template_id = 'fd728f6e-7fa8-11ea-bc55-0242ac130003' where root_org = 'default' and event_id = 'training_register' and recipient = 'manager' and mode = 'email';

INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fd728f6e-7fa8-11ea-bc55-0242ac130003', 'en', 'Your team member has registered for instructor led training - #contentTitle', '<p>Hi #managerName,</p>
<p>Please take a few minutes and consider auto-approved training registration request for user #learnerName.</p>
<p>Course Name&nbsp;&nbsp;&nbsp;&nbsp; :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; #contentTitle</p>
<p>Location&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; #location (City, Country)</p>
<table>
<tbody>
<tr>
<td width="114">
<p>Date</p>
</td>
<td width="150">
<p>Time</p>
</td>
<td width="360">
<p>Venue</p>
</td>
</tr>
<tr>
<td width="114">
<p>#date</p>
</td>
<td width="150">
<p>#time</p>
</td>
<td width="360">
<p>#venue</p>
</td>
</tr>
</tbody>
</table>
<p><br /> You can forward or reject this request within 3 days. <br /> <br /> </p><p>Have a great day! <br /> <br /> Thanks, <br /> Your Wingspan Team</p>
<p>Too many notifications? Click <a href="#appUrl/app/profile/settings?tab=notifications">here</a> to unsubscribe.</p>', current_timestamp, '');






INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-dc39-51ea-a3db-08dc3969zxdx', 'en', 'Check out this #contentType - #contentTitle', '#sharedByName has shared a #contentType #contentTitle with you!', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-dc39-48ea-a3db-08dc3969zxdx', 'en', '#contentType shared - #contentTitle', '<p>Hi #sharedByName,</p>
<p> You have shared a #contentType #contentTitle with following users.</p>
<p>#sharedWithName</p>
<p>Have a great day!</p>
<p> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-dc39-51fa-a3db-08dc3969zxdx', 'en', '#contentType shared - #contentTitle', '#contentType #contentTitle has been shared', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc2eb08-eac9-91fa-a3db-08dc396azxdx', 'en', 'Check out this goal - #contentTitle', '<p>Hi,<br /> #sharedByName has shared a goal #contentTitle with you!</p>
<p>#message</p>
<p>You can accept or reject the goal <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-eac9-91fa-a3db-08dc3969zxdx', 'en', 'Check out this goal - #contentTitle', '#sharedByName has shared a goal #contentTitle with you!', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc2eb08-eac9-91fa-p3f4-08dc396azxdx', 'en', 'Goal shared - #contentTitle', '<p>Hi #sharedByName,</p>
<p>You have shared a goal #contentTitle with following users.</p>
<p>#sharedWithName</p>
<p>You can edit the goal <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-eac9-91fa-p3f4-08dc3969zxdx', 'en', 'Goal shared - #contentTitle', 'Goal with title #contentTitle has been shared!', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b5hteb08-eac9-91fm-a3db-08dc3969zxdx', 'en', 'Check out this playlist - #contentTitle', '#sharedByName has shared a playlist #contentTitle with you!', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b5hteb08-eac9-91fa-p5gt-08dc396azxdx', 'en', 'Playlist shared - #contentTitle', '<p>Hi #sharedByName,</p>
<p>You have shared a playlist #contentTitle with following users.</p>
<p>#sharedWithName</p>
<p>You can edit the playlist <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b5hteb08-eac9-91fa-p5gt-08dc3969zxdx', 'en', 'Playlist shared - #contentTitle', 'Playlist with title #contentTitle has been shared!', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('xxe1db08-dc39-48aa-a3db-08dc3958zxcm', 'en', 'You have completed online #contentType - #contentTitle', '<p>Hi #learnerName</p>
<p>You have successfully completed the online #contentType #contentTitle. You can check out your completion certificate <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('xxe1db08-dc39-48da-a3db-08dc3958zxco', 'en', '#contentType Completion', 'You have completed the #contentType - #contentTitle.', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxa1db08-dc39-48ea-a3db-09dc3958zxdx', 'en', 'You have earned a Badge – #badgeName', '<p>Hi #learnerName</p>
<p>Congratulations! You have successfully earned the #badgeName Badge for meeting the criteria #message.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxa1db08-dc39-48ea-a3db-09dc3958zxea', 'en', 'Badge Earned', 'Congratulations! You have earned the badge - #badgeName.', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-dc39-48ea-a3dc-08dc3958zxde', 'en', 'Content query on #contentType by #learnerName', '<p>Hi #expertName,<br /> <br /> I found your reference while going through the #contentType #contentTitle. I request you to help me with a query.</p>
<p>Query:</p>
<p>#query</p>
<p>&nbsp;</p>
<p>Regards,</p>
<p>#learnerName</p>
<p>Email: #learnerEmail</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-dc39-48ea-a3dc-08dc3958zxdg', 'en', 'Content query on #contentType by #learnerName', '#learnerName requests your help for query - #query', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-dc39-48ea-b3db-08dc3958zxdi', 'en', 'You have requested for help on content from #expertName', '<p>Hi #learnerName,<br /> <br /> Your query on #contentType #contentTitle has been sent to #expertName.</p>
<p>Query:</p>
<p>#query</p>
<p>&nbsp;</p>
<p>Have a great day!<br /> Regards,<br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');

INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-dc39-48ea-b3db-08dc3958zxl', 'en', 'You have requested for help on content from #expertName', 'Your content help query #query is sent to #expertName.', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-dc39-48ea-a3db-09dc3958zxjl', 'en', 'Content Feedback Received', 'You have received feedback for #contentType - #contentTitle.', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1cc08-dc39-48ea-a5db-08dc3958zxdo', 'en', 'Feedback sent for #contentType - #contentTitle', '<p>Hi #learnerName,<br /> <br /> Your feedback for the #contentType #contentTitle has been sent to authors(s).</p>
<p>Feedback:</p>
<p>#feedback</p>
<p>Have a great day!<br /> Regards,<br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1cc08-dc39-48ea-a8db-08dc3958zxdq', 'en', 'Content Feedback Sent', 'You have provided feedback for #contentType - #contentTitle.', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zzc1db08-dc39-48ea-a3db-08dc3958zxdt', 'en', 'Feedback Response Received', 'You have received feedback response for #contentType - #contentTitle.', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1dbf8-dc39-48ea-a3db-08dc3958zxdu', 'en', 'Feedback response sent for #contentType - #contentTitle', '<p>Hi Author(s),<br /> <br /> #authorName has responded to the feedback for the #contentType #contentTitle.</p>
<p>Feedback:</p>
<p>#feedback</p>
<p>Response:</p>
<p>#response</p>
<p>Have a great day!<br /> Regards,<br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1dbf8-dc39-48ea-a3db-08dc3958zxdx', 'en', 'Feedback Response Sent', 'Feedback response sent for #contentType - #contentTitle.', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zzc1db08-ac39-48ea-a3db-08dc3958zxdr', 'en', 'Feedback response received for #contentType - #contentTitle', '<p>Hi #learnerName,<br /> <br /> #authorName has responded to your feedback on #contentType #contentTitle.</p>
<p>Feedback:</p>
<p>#feedback</p>
<p>Response:</p>
<p>#response</p>
<p>Have a great day!<br /> Regards,<br /> The Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc2eb08-dc39-49ea-a3db-08dc396azxdx', 'en', 'Check out this #contentType - #contentTitle', '<p>Hi,</p><p><br /> #sharedByName has shared a #contentType #contentTitle with you! You can view the content <a href="#targetUrl">here</a>.</p> <br/> <p>Message:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<p>#message</p><p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb08-ac39-48ea-a3db-08dc3969zxdx', 'en', 'You are now co-author for #contentType - #contentTitle', '<p>Hi Co-author(s),<br /> <br /> #authorName has added you as co-author for the #contentType #contentTitle.<br /> <br /> You can author the content <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb08-ac39-48ea-a3db-08dc3969zxda', 'en', 'Became Content Co-author', 'You are now co-author for #contentType - #contentTitle.', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b5hteb08-eac9-91fm-a3db-08dc396azvdy', 'en', 'Playlist Shared', '#sharedByName has shared #contentTitle with you', '2020-03-19 00:00:00', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb08-ac39-48ea-a3db-08dc3969zxex', 'en', 'Co-authors added for #contentType - #contentTitle', '<p>Hi Author(s),<br /> <br /> #authorName has successfully added following co-author(s) for the #contentType #contentTitle.</p>
<p>#contributorName</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb08-ac39-48ea-a3db-08dc3969zxea', 'en', 'Co-author Added', 'You have added co-author(s) to a #contentType.', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb08-ad49-48ea-a3db-08dc3969zaex', 'en', '#nextStage request for #contentType - #contentTitle', '<p>Hi #nextActor,<br /> <br /> #authorName has created a #contentType #contentTitle and submitted it to you for #nextStage.</p>
<p>Comment</p>
<p>#comment<br /> <br /> You can accept or reject the #contentType <a href="#targetUrl">here</a>. You can also choose to edit the content during your review process.</p>
<p><strong>&nbsp;</strong></p>
<p>Have a great day! </p>
<p>Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb08-ad49-48ea-a3db-08dc3969zaea', 'en', '#nextStage Request Received', 'You have received #nextStage request for #contentType - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb08-ad49-19ea-a3db-08dc3968zaex', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi Author(s),<br /> <br /> Your authored #contentType #contentTitle is submitted for #nextStage by #authorName. We have informed the #nextActor.</p>
<p>Comment</p>
<p>#comment<br /> <br /> Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb08-ad49-19ea-a3db-08dc3869zaea', 'en', '#nextStage Request Sent', '#nextStage request for #contentType - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb08-ad49-21ea-a3db-08dc3968zbex', 'en', '#nextStage request for #contentType - #contentTitle', '<p>Hi #nextActor,<br /> <br /> #actorName has approved a #contentType #contentTitle and submitted it to you for #nextStage.</p>
<p>You can accept or reject the #contentType <a href="#targetUrl">here</a>. You can also choose to edit the content during your review.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb08-ad49-21ea-a3db-08dc3869zbea', 'en', '#nextStage Request Received', 'You have received #nextStage request for #contentType - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb18-bd59-21ea-a3db-08dc3968zbex', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi Author(s),<br /> <br /> Your authored #contentType #contentTitle has been approved at #currentStage stage. It will now undergo #nextStage process. We have informed the concerned stakeholders.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb18-bd59-21ea-a3db-08dc3869zbea', 'en', '#contentType sent for #nextStage', '#contentType sent for #nextStage - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb18-cd59-2wea-a3db-08dc3968zbex', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi #currentActor,<br /> <br /> The #contentType #contentTitle has been approved at #currentStage stage by #actorName. It will now undergo #nextStage process. We have informed the concerned stakeholders.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb18-cd59-2wea-a3db-08dc3869zbea', 'en', '#contentType sent for #nextStage - #contentTitle', '#contentType sent for #nextStage - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb18-cd69-3wea-a3db-08dc3968zbex', 'en', '#contentType rejected at #currentStage - #contentTitle', '<p>Hi Author(s),<br /> <br /> Your authored #contentType #contentTitle has been rejected at #currentStage stage. You need to improve the content <a href="#targetUrl">here</a> and send it back.</p>
<p>Comment:</p>
<p>#comment</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb18-cd69-3wea-a3db-08dc3869zbea', 'en', '#contentType rejected at #currentStage - #contentTitle', '#contentType rejected at #currentStage - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb18-ed69-3wfa-a3db-08dc3968zbex', 'en', '#contentType rejected at #currentStage - #contentTitle', '<p>Hi #currentActor,<br /> <br /> The #contentType #contentTitle has been rejected at #currentStage stage by #actorName. We have informed the author(s) who needs to make necessary improvements to the content.</p>
<p>Comment:</p>
<p>#comment</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb18-ed69-3wfa-a3db-08dc3869zbea', 'en', '#contentType rejected at #currentStage - #contentTitle', '#contentType rejected at #currentStage - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb22-fd69-3wfa-a3db-08dc3968zbex', 'en', '#contentType published - #contentTitle', '<p>Hi Author(s),<br /> <br /> Congratulations ! Your #contentType #contentTitle is published by #publisherName and is now available for viewing <a href="#targetUrl">here</a>.<br /> <br /> Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb22-fd69-3wfa-a3db-08dc3869zbea', 'en', '#contentType published - #contentTitle', '#contentType published - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb23-fd61-3wfa-a3db-08dc3968zbex', 'en', '#contentType published - #contentTitle', '<p>Hi Publisher(s),<br /> <br /> The #contentType #contentTitle has been published by #publisherName. We have informed the author(s). The content is available for viewing <a href="#targetUrl">here</a>.<br /> <br /> Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb23-fd61-3wfa-a3db-08dc3869zbea', 'en', '#contentType published - #contentTitle', '#contentType published - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb24-fd63-3wfa-a3db-08dc3968zbex', 'en', 'You are now #currentActor for #contentType - #contentTitle', '<p>Hi,<br /> <br /> #actorName has added you as #currentActor for #contentType #contentTitle.<br /> <br /> You can review the content <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>
<p>&nbsp;</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb24-fd63-3wfa-a3db-08dc3869zbea', 'en', 'You are now #currentActor for #contentType - #contentTitle', 'You are now #currentActor for #contentType - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb25-fd65-3wfa-a3db-08dc3968zbex', 'en', '#currentActor added for #contentType - #contentTitle', '<p>Hi,<br /> <br /> #delegatedActorName has been successfully added as #currentActor for the #contentType #contentTitle. <br /> </p>
<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('authdb25-fd65-3wfa-a3db-08dc3869zbea', 'en', '#currentActor added for #contentType - #contentTitle', '#currentActor added for #contentType - #contentTitle', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('1ba957c5-bde6-477a-a861-dd73da6cfc7a', 'en', '#sharedByName has just shared a #contentType with you', '<p>Hi there,</p>
<p>#sharedByName has just shared a #contentType #contentTitle with you on IGoT.<br />You can check out the content <a href="#targetUrl">here</a>.</p>
<p>#message</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('1ba957c5-bde6-477a-a861-dd73da6cfc7a', 'de', '#sharedByName hat gerade einen #contentType mit dir geteilt', '<p>Hi,</p>
<p>#sharedByName hat bei IGoT gerade einen #contentType mit dir geteilt.</p>
<p><a href="#targetUrl">Hier</a> kannst du dir die Inhalte anschauen.</p>
<p>#message</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('1ba957c5-bde6-477a-a862-dd73da6cfc7a', 'en', '#sharedByName has shared a goal #contentTitle', '<p>Hi there,</p>
<p><br />We&rsquo;re just letting you know that #sharedByName has shared a goal #contentTitle with you on IGoT.</p>
<p>Their message:</p>
<p>#message</p>
<p>You can accept or reject the goal <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('1ba957c5-bde6-477a-a862-dd73da6cfc7a', 'de', '#sharedByName hat ein Ziel #contentTitle geteilt', '<p>Hi,</p>
<p><br />nur damit du Bescheid wei&szlig;t: #sharedByName hat bei IGoT ein Ziel #contentTitle mit dir geteilt.</p>
<p>&nbsp;</p>
<p>Nachricht:</p>
<p>#message</p>
<p><a href="#targetUrl">Hier</a> kannst du das Ziel akzeptieren oder ablehnen.</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('1ba957c5-bde6-477a-a863-dd73da6cfc7a', 'en', '#sharedByName has shared a playlist #contentTitle', '<p>Hi there,</p>
<p>We&rsquo;re just letting you know that #sharedByName has shared a playlist #contentTitle with you on IGoT.</p>
<p>Their message:</p>
<p>#message</p>
<p>You can accept or reject the playlist <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('1ba957c5-bde6-477a-a863-dd73da6cfc7a', 'de', '#sharedByName hat eine Playlist #contentTitle geteilt', '<p>Hi,</p>
<p>nur damit du Bescheid wei&szlig;t: #sharedByName hat bei IGoT eine Playlist #contentTitle mit dir geteilt.</p>
<p>Nachricht:</p>
<p>#message</p>
<p><a href="#targetUrl">Hier</a> kannst du die Playlist akzeptieren oder ablehnen.</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb442', 'en', 'You have been added as a co-author', '<p>Hello,<br /> <br /> Just to let you know: #authorName has added you as a co-author for the #contentType #contentTitle on IGoT.<br /> <br /> You can start editing the available content right <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,<br /> <br /> The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb442', 'de', 'Du wurdest als Co-Autor hinzugefügt', '<p>Hallo,<br /> <br /> nur damit du Bescheid wei&szlig;t: #authorName hat dich f&uuml;r #contentType #contentTitle bei IGoT als Co-Autor hinzugef&uuml;gt.</p>
<p>&nbsp;</p>
<p><a href="#targetUrl">Hier</a>kannst du die Inhalte bearbeiten.</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb443', 'en', 'New co-authors have been added', '<p>Hello,<br /> <br /> We just wanted to let you know that #authorName has added the following co-author(s) for the #contentType #contentTitle on IGoT.</p>
<p>#contributorName</p>
<p>&nbsp;</p>
<p>Thanks for now,<br /> <br /> The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb443', 'de', 'Neue Co-Autoren wurden hinzugefügt', '<p>Hallo,<br /> <br /> wir wollten dich nur wissen lassen, dass #authorName folgende(n) Co-Autor(en) f&uuml;r #contentType #contentTitle bei IGoT hinzugef&uuml;gt hat.</p>
<p>&nbsp;</p>
<p>#contributorName</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb444', 'en', 'Please review some new content', '<p>Hi #nextActor,<br /> <br /> Just a quick note to say that #authorName has created a #contentType #contentTitle on IGoT. And they&rsquo;ve just shared it with you for #nextStage.<br /> </p>
<p>Their comments:</p>
<p>#comment<br /> <br /> You can accept or reject the #contentType <a href="#targetUrl">here</a>. You can also edit the content during your review.</p>
<p><strong>&nbsp;</strong></p>
<p>Thanks for now,<br /> <br /> The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb444', 'de', 'Bitte überprüfe neue Inhalte', '<p>Hi #nextActor,<br /> <br /> nur eine kurze Info: #authorName hat #contentType #contentTitle bei IGoT erstellt. Und dies gerade mit dir f&uuml;r #nextStage geteilt.<br /> </p>
<p>Hier die Kommentare:</p>
<p>#comment<br /> <br /> <a href="#targetUrl">Hier</a> kannst du #contentType akzeptieren, ablehnen oder einfach selbst bearbeiten.</p>
<p><strong>&nbsp;</strong></p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb445', 'en', 'Your content has been submitted', '<p>Hello,<br /> </p>
<p>The content you created on IGoT, #contentType #contentTitle, has been submitted for #nextStage by #authorName. We&rsquo;ve let the #nextActor know.</p>
<p>&nbsp;</p>
<p>Their comments:</p>
<p>#comment<br /> <br /> Thanks for now,<br /> <br /> The IGoT Team</p>
<p>&nbsp;</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb445', 'de', 'Deine Inhalte wurden übermittelt', '<p>Hallo,</p>
<p>die von dir erstellten Inhalte bei IGoT, #contentType #contentTitle, wurden f&uuml;r #nextStage an #authorName &uuml;bermittelt. Wir haben #nextActor benachrichtigt.</p>
<p>&nbsp;</p>
<p>Hier die Kommentare:</p>
<p>#comment<br /> <br /> Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb446', 'en', 'Please review some new content', '<p>Hi #nextActor,<br /> <br /> We&rsquo;re letting you know that #actorName has approved a #contentType #contentTitle and submitted it to you for #nextStage on IGoT.</p>
<p>&nbsp;</p>
<p>You can accept or reject the #contentType <a href="#targetUrl">here</a>. You can also edit the content during your review.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p><br /> The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb446', 'de', 'Bitte überprüfe neue Inhalte', '<p>Hi #nextActor,<br /> <br /> #actorName hat #contentType #contentTitle best&auml;tigt und f&uuml;r #nextStage bei IGoT wieder an dich &uuml;bermittelt.</p>
<p>&nbsp;</p>
<p><a href="#targetUrl">Hier</a> kannst du #contentType akzeptieren, ablehnen oder einfach selbst bearbeiten.</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb447', 'en', 'Your content has been approved at #currentStage', '<p>Hello,<br /> <br /> Here&rsquo;s a quick update. The content you created on IGoT, #contentType #contentTitle, has been approved at #currentStage. It will now go through the #nextStage process. We&rsquo;ve let the relevant stakeholders know.<br /> </p>
<p>Thanks for now,<br /> The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb447', 'de', 'Deine Inhalte wurden akzeptiert in #currentStage', '<p>Hallo,<br /> <br /> hier ein kurzes Update. Die von dir erstellten Inhalte bei IGoT, #contentType #contentTitle, wurden in #currentStage akzeptiert. Sie durchlaufen jetzt den #nextStage-Prozess. Wir haben alle relevanten Personen benachrichtigt.<br /> </p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('63724352-7254-11ea-bc55-0242ac130003', 'en', '', '<p>Web: <a href="#appUrl">#appUrl</a></p>
<p>E-Mail: <a href="mailto:#appEmail">#appEmail</a></p>', '2019-07-01 08:49:16.556', '');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb448', 'en', 'The #contentType #contentTitle has been approved', '<p>Hi #currentActor,<br /> <br /> Just to let you know: the #contentType #contentTitle on IGoT has been approved at #currentStage by #actorName. Now it&rsquo;s ready to go through the #nextStage process. We&rsquo;ve let the relevant stakeholders know.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb448', 'de', 'Der #contentType #contentTitle wurde akzeptiert', '<p>Hi #currentActor,<br /> <br /> nur damit du Bescheid wei&szlig;t: Der #contentType #contentTitle bei IGoT wurde von #actorName in #currentStage akzeptiert. Jetzt kann er den #nextStage-Prozess durchlaufen. Wir haben alle relevanten Personen benachrichtigt.</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb449', 'en', 'Your #contentType #contentTitle has been rejected', '<p>Hello,<br /> <br /> We&rsquo;re sorry: the content you created on IGoT, #contentType #contentTitle, has been rejected at #currentStage. You&rsquo;ll need to update the content <a href="#targetUrl">here</a> and send it back for approval.</p>
<p>&nbsp;</p>
<p>Comments:</p>
<p>#comment</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb449', 'de', 'Dein #contentType #contentTitle wurde abgelehnt', '<p>Hallo,<br /> <br /> es tut uns leid: Die von dir erstellten Inhalte bei IGoT, #contentType #contentTitle, wurden in #currentStage abgelehnt. <a href="#targetUrl">Hier</a> kannst du deine Inhalte bearbeiten und zur Best&auml;tigung zur&uuml;cksenden.</p>
<p>&nbsp;</p>
<p>Kommentare:</p>
<p>#comment</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb450', 'en', 'The #contentType #contentTitle has been rejected', '<p>Hi #currentActor,<br /> </p>
<p>The #contentType #contentTitle on IGoT has been rejected at #currentStage by #actorName. We&rsquo;ve asked its author(s) to update the content and send it again for approval.</p>
<p>&nbsp;</p>
<p>Comments:</p>
<p>#comment</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb450', 'de', 'Der #contentType #contentTitle wurde abgelehnt', '<p>Hi #currentActor,<br /> </p>
<p>der #contentType #contentTitle bei IGoT wurde in #currentStage von #actorName abgelehnt. Wir haben die Autoren gebeten, die Inhalte nochmals zu bearbeiten und dann erneut zur Best&auml;tigung zur&uuml;ckzusenden.</p>
<p>&nbsp;</p>
<p>Kommentare:</p>
<p>#comment</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb451', 'en', 'Your #contentType #contentTitle has been published', '<p>Hello,<br /> <br /> Good news! Your #contentType #contentTitle has been published by #publisherName on IGoT. You can view it <a href="#targetUrl">here</a>.<br /> <br /> Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb451', 'de', 'Dein #contentType #contentTitle wurde veröffentlicht', '<p>Hallo,<br /> <br /> gute Neuigkeiten! Dein #contentType #contentTitle wurde von #publisherName bei IGoT ver&ouml;ffentlicht. <a href="#targetUrl">Hier</a> kannst du es dir anschauen.<br /> <br /> Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb452', 'en', 'Your #contentType #contentTitle has been published', '<p>Hello,<br /> <br /> The #contentType #contentTitle has just been published by #publisherName on IGoT. We&rsquo;ve let the author(s) know and the content can be viewed <a href="h#targetUrl">here</a>.<br /> <br /> Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b31c2e15-4d60-4317-ae27-78c941adb452', 'de', 'Dein #contentType #contentTitle wurde veröffentlicht', '<p>Hallo,</p>
<p>der #contentType #contentTitle wurde von #publisherName bei IGoT ver&ouml;ffentlicht. Wir haben den/die Autor/-en benachrichtigt. Die Inhalte findest du <a href="#targetUrl">hier</a>.<br /> <br /> Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b41c2e15-4d60-4317-ae27-78c941adb452', 'en', 'You have been added as #currentActor for #contentType #contentTitle', '<p>Hello,</p>
<p>We&rsquo;re letting you know that #actorName has added you as #currentActor for #contentType #contentTitle on IGoT.<br /> <br /> You can review the content <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b41c2e15-4d60-4317-ae27-78c941adb452', 'de', 'Du wurdest als #currentActor für #contentType #contentTitle hinzugefügt', '<p>Hallo,<br /> <br /> wir schreiben dir, weil #actorName dich als #currentActor f&uuml;r #contentType #contentTitle bei IGoT hinzugef&uuml;gt hat.<br /> </p>
<p><a href="#targetUrl">Hier</a> kannst du die Inhalte einsehen.</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b51c2e15-4d60-4317-ae27-78c941adb452', 'en', '#delegatedActorName have been successfully added as #currentActor', '<p>Hello,</p>
<p>Just to confirm: we&rsquo;ve successfully added #delegatedActorName as #currentActor for the #contentType #contentTitle on IGoT.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b51c2e15-4d60-4317-ae27-78c941adb452', 'de', '#delegatedActorName haben #currentActor erfolgreich hinzugefügt', '<p>Hallo,<br /> <br /> kurze Info: Wir haben #delegatedActorName erfolgreich als #currentActor f&uuml;r den #contentType #contentTitle bei IGoT hinzugef&uuml;gt.</p>
<p>&nbsp;</p>
<p>Bis dann<br /> <br /> dein IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('b5hteb08-eac9-91fm-a3db-08dc396azxdx', 'en', 'Check out this playlist - #contentTitle', '<p>Hi there,</p>
<p>We&rsquo;re just letting you know that #sharedByName has shared the current state of the playlist #contentTitle with you on IGoT.</p>
<p>Their message:</p>
<p>#message</p>
<p>You can accept or reject the playlist <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('ce553511-1812-11ea-b44e-cbcdf7e2e1f1', 'en', 'Check out this playlist - #contentTitle', '<p>Hi there,</p>
<p>We&rsquo;re just letting you know that #sharedByName has shared the current state of the playlist #contentTitle with you on IGoT.</p>
<p>Their message:</p>
<p>#message</p>
<p>You can accept or reject the playlist <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The My IGoT Team</p>', '2019-12-06 10:26:03.839', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('01656972-1813-11ea-b44e-9f63e31ef225', 'en', 'Playlist shared - #contentTitle', '<p>Hi #sharedByName,</p>
<p>You have shared a playlist #contentTitle with following users.</p>
<p>#sharedWithName</p>
<p>You can edit the playlist <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT Team</p>', '2019-12-06 10:27:29.509', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('59b2b293-1813-11ea-b44e-8992dfcfdae1', 'en', 'Check out this playlist - #contentTitle', '<p>Hi there,</p>
<p>We&rsquo;re just letting you know that #sharedByName has shared the current state of the playlist #contentTitle with you on IGoT.</p>
<p>Their message:</p>
<p>#message</p>
<p>You can accept or reject the playlist <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-12-06 10:29:57.655', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('28e091e4-1814-11ea-b44e-0b2d8a6a37c4', 'en', 'Goal shared - #contentTitle', '<p>Hi #sharedByName,</p>
<p>You have shared a goal #contentTitle with following users.</p>
<p>#sharedWithName</p>
<p>You can edit the goal <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT Team</p>', '2019-12-06 10:35:45.244', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('4daac095-1814-11ea-b44e-136194c0b870', 'en', 'Check out this goal - #contentTitle', '<p>Hi,<br /> #sharedByName has shared a goal #contentTitle with you!</p>
<p>#message</p>
<p>You can accept or reject the goal <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT Team</p>', '2019-12-06 10:36:46.967', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('bad400f6-1814-11ea-b44e-c9d9df2bc5f0', 'en', '#contentType shared - #contentTitle', '<p>Hi #sharedByName,</p>
<p> You have shared a #contentType #contentTitle with following users.</p>
<p>#sharedWithName</p>
<p>Have a great day!</p>
<p> Regards, <br /> The IGoT Team</p>', '2019-12-06 10:39:50.109', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f5edc54b-1a44-11ea-8e80-77a12faeae5b', 'en', 'You are now co-author for #contentType - #contentTitle', '<p>Hi Co-author(s),<br /> <br /> #authorName has added you as co-author for the #contentType #contentTitle.<br /> <br /> You can author the content <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:07.424', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f620e33c-1a44-11ea-8e80-8d1e33338855', 'en', 'Co-authors added for #contentType - #contentTitle', '<p>Hi Author(s),<br /> <br /> #authorName has successfully added following co-author(s) for the #contentType #contentTitle.</p>
<p>#contributorName</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:07.767', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f663436d-1a44-11ea-8e80-dbfd3094cee5', 'en', 'You are now co-author for #contentType - #contentTitle', '<p>Hi Co-author(s),<br /> <br /> #authorName has added you as co-author for the #contentType #contentTitle.<br /> <br /> You can author the content <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:08.202', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f68aefae-1a44-11ea-8e80-5d470ad47757', 'en', 'Co-authors added for #contentType - #contentTitle', '<p>Hi Author(s),<br /> <br /> #authorName has successfully added following co-author(s) for the #contentType #contentTitle.</p>
<p>#contributorName</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:08.462', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f6cf249f-1a44-11ea-8e80-a90ee9124e1d', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi Author(s),<br /> <br /> Your authored #contentType #contentTitle is submitted for #nextStage by #authorName. We have informed the #nextActor.</p>
<p>Comment</p>
<p>#comment<br /> <br /> Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:08.909', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f70305e0-1a44-11ea-8e80-790400f8c858', 'en', '#nextStage request for #contentType - #contentTitle', '<p>Hi #nextActor,<br /> <br /> #authorName has created a #contentType #contentTitle and submitted it to you for #nextStage.</p>
<p>Comment</p>
<p>#comment<br /> <br /> You can accept or reject the #contentType <a href="#targetUrl">here</a>. You can also choose to edit the content during your review process.</p>
<p><strong>&nbsp;</strong></p>
<p>Have a great day! </p>
<p>Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:09.249', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f746ecb1-1a44-11ea-8e80-b3c8f349a4cc', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi Author(s),<br /> <br /> Your authored #contentType #contentTitle is submitted for #nextStage by #authorName. We have informed the #nextActor.</p>
<p>Comment</p>
<p>#comment<br /> <br /> Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:09.693', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f76e71e2-1a44-11ea-8e80-cfb9cd92dfb3', 'en', '#nextStage request for #contentType - #contentTitle', '<p>Hi #nextActor,<br /> <br /> #authorName has created a #contentType #contentTitle and submitted it to you for #nextStage.</p>
<p>Comment</p>
<p>#comment<br /> <br /> You can accept or reject the #contentType <a href="#targetUrl">here</a>. You can also choose to edit the content during your review process.</p>
<p><strong>&nbsp;</strong></p>
<p>Have a great day! </p>
<p>Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:09.953', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f7bd7c43-1a44-11ea-8e80-3736f5cadc6f', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi #currentActor,<br /> <br /> The #contentType #contentTitle has been approved at #currentStage stage by #actorName. It will now undergo #nextStage process. We have informed the concerned stakeholders.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:10.471', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f7e6af24-1a44-11ea-8e80-7d6a6ceac5c6', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi Author(s),<br /> <br /> Your authored #contentType #contentTitle has been approved at #currentStage stage. It will now undergo #nextStage process. We have informed the concerned stakeholders.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:10.741', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f812a125-1a44-11ea-8e80-0592505ceed2', 'en', '#nextStage request for #contentType - #contentTitle', '<p>Hi #nextActor,<br /> <br /> #actorName has approved a #contentType #contentTitle and submitted it to you for #nextStage.</p>
<p>You can accept or reject the #contentType <a href="#targetUrl">here</a>. You can also choose to edit the content during your review.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:11.029', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f855c4a6-1a44-11ea-8e80-a38fc6c3dbff', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi #currentActor,<br /> <br /> The #contentType #contentTitle has been approved at #currentStage stage by #actorName. It will now undergo #nextStage process. We have informed the concerned stakeholders.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:11.469', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('716e7b8e-1d8a-11ea-978f-2e728ce88125', 'en', 'Your content has been deleted', '<p>Hello,<br /> <br /> Some of the content authored by you has expired and hence, deleted.<br /> <br /> You can view all your deleted content <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>Wingspan Team</p>
', '2019-12-13 09:38:26.311661', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f8805717-1a44-11ea-8e80-dfcb922e2476', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi Author(s),<br /> <br /> Your authored #contentType #contentTitle has been approved at #currentStage stage. It will now undergo #nextStage process. We have informed the concerned stakeholders.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:11.748', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f8b12b18-1a44-11ea-8e80-d395199e56ea', 'en', '#nextStage request for #contentType - #contentTitle', '<p>Hi #nextActor,<br /> <br /> #actorName has approved a #contentType #contentTitle and submitted it to you for #nextStage.</p>
<p>You can accept or reject the #contentType <a href="#targetUrl">here</a>. You can also choose to edit the content during your review.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:12.068', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f96a1ad9-1a44-11ea-8e80-11332eb0bcd9', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi #currentActor,<br /> <br /> The #contentType #contentTitle has been approved at #currentStage stage by #actorName. It will now undergo #nextStage process. We have informed the concerned stakeholders.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:13.28', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f993e9fa-1a44-11ea-8e80-172038b22a3f', 'en', '#contentType sent for #nextStage - #contentTitle', '<p>Hi Author(s),<br /> <br /> Your authored #contentType #contentTitle has been approved at #currentStage stage. It will now undergo #nextStage process. We have informed the concerned stakeholders.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:13.554', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('f9bb210b-1a44-11ea-8e80-4d373e43f109', 'en', '#nextStage request for #contentType - #contentTitle', '<p>Hi #nextActor,<br /> <br /> #actorName has approved a #contentType #contentTitle and submitted it to you for #nextStage.</p>
<p>You can accept or reject the #contentType <a href="#targetUrl">here</a>. You can also choose to edit the content during your review.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:13.811', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fa2888dc-1a44-11ea-8e80-eb53baa36df2', 'en', '#contentType rejected at #currentStage - #contentTitle', '<p>Hi #currentActor,<br /> <br /> The #contentType #contentTitle has been rejected at #currentStage stage by #actorName. We have informed the author(s) who needs to make necessary improvements to the content.</p>
<p>Comment:</p>
<p>#comment</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:14.528', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fa516d9d-1a44-11ea-8e80-e36b9ad6dae4', 'en', '#contentType rejected at #currentStage - #contentTitle', '<p>Hi Author(s),<br /> <br /> Your authored #contentType #contentTitle has been rejected at #currentStage stage. You need to improve the content <a href="#targetUrl">here</a> and send it back.</p>
<p>Comment:</p>
<p>#comment</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:14.796', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fa8f60fe-1a44-11ea-8e80-89e6aac50254', 'en', '#contentType rejected at #currentStage - #contentTitle', '<p>Hi #currentActor,<br /> <br /> The #contentType #contentTitle has been rejected at #currentStage stage by #actorName. We have informed the author(s) who needs to make necessary improvements to the content.</p>
<p>Comment:</p>
<p>#comment</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:15.202', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fab81eaf-1a44-11ea-8e80-03228a5c92c6', 'en', '#contentType rejected at #currentStage - #contentTitle', '<p>Hi Author(s),<br /> <br /> Your authored #contentType #contentTitle has been rejected at #currentStage stage. You need to improve the content <a href="#targetUrl">here</a> and send it back.</p>
<p>Comment:</p>
<p>#comment</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:15.469', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fafd8c20-1a44-11ea-8e80-d5b9e68526b5', 'en', '#contentType published - #contentTitle', '<p>Hi Author(s),<br /> <br /> Congratulations ! Your #contentType #contentTitle is published by #publisherName and is now available for viewing <a href="#targetUrl">here</a>.<br /> <br /> Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:15.924', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fb25d4a1-1a44-11ea-8e80-bff87783956f', 'en', '#contentType published - #contentTitle', '<p>Hi Publisher(s),<br /> <br /> The #contentType #contentTitle has been published by #publisherName. We have informed the author(s). The content is available for viewing <a href="#targetUrl">here</a>.<br /> <br /> Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:16.188', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fb64b262-1a44-11ea-8e80-add725b97350', 'en', '#contentType published - #contentTitle', '<p>Hi Author(s),<br /> <br /> Congratulations ! Your #contentType #contentTitle is published by #publisherName and is now available for viewing <a href="#targetUrl">here</a>.<br /> <br /> Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:16.6', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fb8b2623-1a44-11ea-8e80-03fcd32ba1af', 'en', '#contentType published - #contentTitle', '<p>Hi Publisher(s),<br /> <br /> The #contentType #contentTitle has been published by #publisherName. We have informed the author(s). The content is available for viewing <a href="#targetUrl">here</a>.<br /> <br /> Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:16.852', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fbcd1124-1a44-11ea-8e80-474fd151c212', 'en', '#currentActor added for #contentType - #contentTitle', '<p>Hi,<br /> <br /> #delegatedActorName has been successfully added as #currentActor for the #contentType #contentTitle. <br /> </p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:17.284', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fbf29a85-1a44-11ea-8e80-93a05c4a14dd', 'en', 'You are now #currentActor for #contentType - #contentTitle', '<p>Hi,<br /> <br /> #actorName has added you as #currentActor for #contentType #contentTitle.<br /> <br /> You can review the content <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>
<p>&nbsp;</p>', '2019-12-09 11:00:17.53', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fc308de6-1a44-11ea-8e80-4dfa508608ed', 'en', '#currentActor added for #contentType - #contentTitle', '<p>Hi,<br /> <br /> #delegatedActorName has been successfully added as #currentActor for the #contentType #contentTitle. <br /> </p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-09 11:00:17.936', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('fc586137-1a44-11ea-8e80-2b58eae259ac', 'en', 'You are now #currentActor for #contentType - #contentTitle', '<p>Hi,<br /> <br /> #actorName has added you as #currentActor for #contentType #contentTitle.<br /> <br /> You can review the content <a href="#targetUrl">here</a>.</p>
<p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>
<p>&nbsp;</p>', '2019-12-09 11:00:18.197', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('e17f2bcd-1d6d-11ea-8f1d-5597abea5279', 'en', 'Content query on #contentType by #learnerName', '<p>Hi #expertName,<br /> <br /> I found your reference while going through the #contentType #contentTitle. I request you to help me with a query.</p>
<p>Query:</p>
<p>#query</p>
<p>&nbsp;</p>
<p>Regards,</p>
<p>#learnerName</p>
<p>Email: #learnerEmail</p>', '2019-12-13 06:00:36.006', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('zxc1db08-dc39-48ea-a3db-09dc3958zxdl', 'en', 'Feedback received for #contentType - #contentTitle', '<p>Hi Author(s),<br /> <br /> #learnerName has provided feedback for the #contentType #contentTitle.</p> <p>Feedback:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p><p>#feedback</p><p>You can respond to this feedback <a href="#targetUrl">here</a>.</p> <p>&nbsp;</p><p>Have a great day!<br /> Regards,<br /> The Wingspan Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('6deded0e-1d80-11ea-8f1d-1b95ca285dd4', 'en', 'Feedback response received for #contentType - #contentTitle', '<p>Hi #learnerName,<br /> <br /> #authorName has responded to your feedback on #contentType #contentTitle.</p>
<p>Feedback:</p>
<p>#feedback</p>
<p>Response:</p>
<p>#response</p>
<p>Have a great day!<br /> Regards,<br /> The IGoT Team</p>', '2019-12-13 08:13:22.555', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('6e346bef-1d80-11ea-8f1d-b707f204c9ac', 'en', 'Feedback received for #contentType - #contentTitle', '<p>Hi Author(s),<br /> <br /> #learnerName has provided feedback for the #contentType #contentTitle.</p> <p>Feedback:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p><p>#feedback</p><p>You can respond to this feedback <a href="#targetUrl">here</a>.</p> <p>&nbsp;</p><p>Have a great day!<br /> Regards,<br /> The IGoT Team</p>', '2019-12-13 08:13:23.017', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('093b99a0-1d88-11ea-8f1d-5b27418fa0a9', 'en', 'Feedback response received for #contentType - #contentTitle', '<p>Hi #learnerName,<br /> <br /> #authorName has responded to your feedback on #contentType #contentTitle.</p>
<p>Feedback:</p>
<p>#feedback</p>
<p>Response:</p>
<p>#response</p>
<p>Have a great day!<br /> Regards,<br /> The IGoT Team</p>', '2019-12-13 09:07:49.588', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('098329f1-1d88-11ea-8f1d-b5fed3da43ef', 'en', 'Feedback received for #contentType - #contentTitle', '<p>Hi Author(s),<br /> <br /> #learnerName has provided feedback for the #contentType #contentTitle.</p> <p>Feedback:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p><p>#feedback</p><p>You can respond to this feedback <a href="#targetUrl">here</a>.</p> <p>&nbsp;</p><p>Have a great day!<br /> Regards,<br /> The IGoT Team</p>', '2019-12-13 09:07:50.057', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('3ad27f02-1d89-11ea-8f1d-6752855549ed', 'en', 'Check out this #contentType - #contentTitle', '<p>Hi,</p><p><br/> #sharedByName has shared a #contentType #contentTitle with you! You can view the content <a href="#targetUrl">here</a>.</p> <br/> <p>Message:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<p>#message</p><p>Have a great day! <br /> <br /> Regards, <br /> The IGoT team</p>', '2019-12-13 09:16:22.282', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('085bd33a-1d8a-11ea-978f-2e728ce88125', 'en', 'Your content is going to expire shortly', '<p>Hello,<br /> <br /> Some of the contents you have authored is going to expire within next #noOfDays days. You can choose to confirm these content for deletion OR extend them by increasing their expiry dates. These content will be deleted if you do not act before the expiry date.<br /> <br /> You can view and take action on these content <a href="#targetUrl">here</a>.</p><p>&nbsp;</p><p>Thanks for now,</p><p>The IGoT Team</p>', '2019-12-13 09:38:25.553526', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('085bd33a-1d8a-11ea-978f-2e728ce88125', 'de', 'Dein Inhalt wird in K&uuml;rze ablaufen.', '<p>Hallo,</p><p>einige der von dir verfassten Inhalte werden innerhalb der n&auml;chsten #noOfDays Tage ablaufen. Du kannst diese Inhalte zum L&ouml;schen freigeben oder verl&auml;ngern, indem Du das Ablaufdatum erh&ouml;hst. Diese Inhalte werden gel&ouml;scht, wenn Sie nicht innerhalb der Frist bearbeitet werden.</p><p>Du kannst diese Inhalte <a href="#targetUrl">hier</a> einsehen und &Auml;nderungen vornehmen.</p><p>&nbsp;</p><p>Dein IGoT Team</p>', '2019-12-13 09:38:25.589917', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('085bd60a-1d8a-11ea-978f-2e728ce88125', 'en', 'Your content has been deleted', '<p>Hello,<br /> <br /> Some of the content authored by you has expired and hence, deleted.<br /> <br /> You can view all your deleted content <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The IGoT Team</p>', '2019-12-13 09:38:25.63066', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('085bd60a-1d8a-11ea-978f-2e728ce88125', 'de', 'Dein Inhalt wurde gel&ouml;scht.', '<p>Hallo,</p>
<p>&nbsp;</p>
<p>einige Deiner Inhalte sind abgelaufen und wurden gel&ouml;scht.</p>
<p>&nbsp;</p>
<p><a href="#targetUrl">Hier</a> k&ouml;nnen Sie alle Ihre gel&ouml;schten Inhalte einsehen.</p>
<p>&nbsp;</p>
<p>Dein IGoT Team</p>', '2019-12-13 09:38:25.933292', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('085bd858-1d8a-11ea-978f-2e728ce88125', 'en', 'Knowledge Board #contentTitle updated', '<p>Hello,</p>
<p>We have added new content to the Knowledge Board #contentTitle you are following.</p>
<p>&nbsp;</p>
<p>You can visit the Knowledge Board <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p><p>The IGoT Team</p>', '2019-12-13 09:38:26.023783', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('085bd858-1d8a-11ea-978f-2e728ce88125', 'de', 'Knowledge Board #contentTitle aktualisiert', '<p>Hallo,</p>
<p>&nbsp;</p>
<p>dem Knowledge Board #contentTitle, dem Sie folgen, wurden neue Inhalte hinzugef&uuml;gt.</p>
<p>&nbsp;</p>
<p>Sie k&ouml;nnen das Knowledge Board <a href="#targetUrl">hier</a> besuchen.</p>
<p>&nbsp;</p>
<p>Dein IGoT Team</p>', '2019-12-13 09:38:26.0719', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('716e730a-1d8a-11ea-978f-2e728ce88125', 'en', 'Your content is going to expire shortly', 'Some of the contents you have authored is going to expire within next #noOfDays days.', '2019-12-13 09:38:26.108345', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('716e77a6-1d8a-11ea-978f-2e728ce88125', 'en', 'Your content has been deleted', 'Some of the content authored by you has expired and hence, deleted.', '2019-12-13 09:38:26.14834', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('716e7918-1d8a-11ea-978f-2e728ce88125', 'en', 'Knowledge Board #contentTitle updated', 'We have added new content to the Knowledge Board #contentTitle you are following.', '2019-12-13 09:38:26.18426', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('716e7cc4-1d8a-11ea-978f-2e728ce88125', 'en', 'Knowledge Board #contentTitle updated', '<p>Hello,</p>
<p>We have added new content to the Knowledge Board #contentTitle you are following.</p>
<p>&nbsp;</p>
<p>You can visit the Knowledge Board <a href="#targetUrl">here</a>.</p>
<p>&nbsp;</p>
<p>Thanks for now,</p><p>Wingspan Team</p>', '2019-12-13 09:38:26.247761', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('716e7a58-1d8a-11ea-978f-2e728ce88125', 'en', 'Your content is going to expire shortly', '<p>Hello,<br /> <br /> Some of the contents you have authored is going to expire within next #noOfDays days. You can choose to confirm these content for deletion OR extend them by increasing their expiry dates. These content will be deleted if you do not act before the expiry date.<br /> <br /> You can view and take action on these content <a href="#targetUrl">here</a>.</p>
    <p>&nbsp;</p>
    <p>Thanks for now,</p>
    <p>Wingspan Team</p>', '2019-12-13 09:38:28.958565', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('dd126030-337e-11ea-978f-2e728ce88125', 'en', '#sharedByName has just shared a #contentType with you', '<p>Hi there,</p>
<p>#sharedByName has just shared a #contentType #contentTitle with you on My Learning World.<br />You can check out the content <a href="#targetUrl">here</a>.</p>
<p>#message</p>
<p>&nbsp;</p>
<p>Thanks for now,</p>
<p>The My Learning World Team</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('31bfc794-3b45-11ea-b77f-2e728ce88125', 'en', 'Platform feedback received', '<p>Hi Team,<br /> <br /> <p>#feedback</p><br /> Regards,<br /> #learnerName</p>', '2019-07-01 08:49:16.556', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('2e732ce7-3b6f-11ea-aa2a-e7d9ecabff5e', 'en', 'Platform feedback', '<p>Hi Team,<br /> <br /> <p>#feedback</p><br /> Regards,<br /> #learnerName</p>', '2020-01-20 15:55:29.481', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('6e6ee27e-3b7e-11ea-81ed-655d0343ee2d', 'en', 'Platform feedback', '<p>Hi Team,<br /> <br /> <p>#feedback</p><br /> Regards,<br /> #learnerName</p>', '2020-01-20 17:44:39.263', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('512eacdf-3b7f-11ea-81ed-79e401d286a7', 'en', 'Platform feedback', '<p>Hi Team,<br /> <br /> <p>#feedback</p><br /> Regards,<br /> #learnerName</p>', '2020-01-20 17:50:59.712', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('7a5ef4d0-3b7f-11ea-81ed-7f5a081dddc0', 'en', 'Platform feedback', '<p>Hi Team,<br /> <br /> <p>#feedback</p><br /> Regards,<br /> #learnerName</p>', '2020-01-20 17:52:08.815', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('8016e271-3b7f-11ea-81ed-37b65af4861f', 'en', 'Platform feedback', '<p>Hi Team,<br /> <br /> <p>#feedback</p><br /> Regards,<br /> #learnerName</p>', '2020-01-20 17:52:18.409', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('1fd22682-3b80-11ea-81ed-43d0ee646af2', 'en', 'Platform feedback', '<p>Hi Team,<br /> <br /> <p>#feedback</p><br /> Regards,<br /> #learnerName</p>', '2020-01-20 17:56:43.764', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('44d47c33-3b80-11ea-81ed-6fd688036161', 'en', 'Platform feedback', '<p>Hi Team,<br /> <br /> <p>#feedback</p><br /> Regards,<br /> #learnerName</p>', '2020-01-20 17:57:48.485', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_template (template_id, language, template_subject, template_text, updated_on, updated_by) VALUES ('e58d32d9-3b80-11ea-9882-a742a3784c47', 'en', 'Platform feedback', '<p>Hi Team,<br /> <br /> <p>#feedback</p><br /> Regards,<br /> #learnerName</p>', '2020-01-20 12:32:18.128', 'admin@eagle.com');





INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'share_content', 'sharedWith', 'email', true, '3ad27f02-1d89-11ea-8f1d-6752855549ed', '2019-12-13 09:16:22.282', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'content_expiry_reminder', 'author', 'email', true, '085bd33a-1d8a-11ea-978f-2e728ce88125', '2019-12-13 09:34:54.58859', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'content_expired', 'author', 'email', true, '085bd60a-1d8a-11ea-978f-2e728ce88125', '2019-12-13 09:34:54.619251', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'content_added_to_kb', 'follower', 'email', false, '085bd858-1d8a-11ea-978f-2e728ce88125', '2019-12-13 09:34:55.864614', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'publish_content', 'publisher', 'email', false, 'fb8b2623-1a44-11ea-8e80-03fcd32ba1af', '2019-12-09 11:00:16.852', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'platform_feedback', 'learner', 'email', true, 'e58d32d9-3b80-11ea-9882-a742a3784c47', '2020-01-20 12:32:18.128', 'admin@eagle.com', 'admin@eagle.com');
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'share_playlist', 'sharedWith', 'push', true, NULL, '2020-03-19 21:07:36.274', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'share_playlist', 'sharedWith', 'email', true, '59b2b293-1813-11ea-b44e-8992dfcfdae1', '2019-12-06 10:29:57.655', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'share_goal', 'sharedWith', 'email', true, '4daac095-1814-11ea-b44e-136194c0b870', '2019-12-06 10:36:46.967', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'share_playlist', 'sharedBy', 'email', false, '01656972-1813-11ea-b44e-9f63e31ef225', '2019-12-06 10:27:29.509', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'add_contributor', 'author', 'email', true, 'f68aefae-1a44-11ea-8e80-5d470ad47757', '2019-12-09 11:00:08.462', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'add_contributor', 'contributor', 'email', true, 'f663436d-1a44-11ea-8e80-dbfd3094cee5', '2019-12-09 11:00:08.202', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'send_content', 'nextActor', 'email', true, 'f76e71e2-1a44-11ea-8e80-cfb9cd92dfb3', '2019-12-09 11:00:09.953', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'approve_content', 'nextActor', 'email', true, 'f9bb210b-1a44-11ea-8e80-4d373e43f109', '2019-12-09 11:00:13.811', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'send_content', 'author', 'email', true, 'f746ecb1-1a44-11ea-8e80-b3c8f349a4cc', '2019-12-09 11:00:09.693', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'reject_content', 'author', 'email', true, 'fab81eaf-1a44-11ea-8e80-03228a5c92c6', '2019-12-09 11:00:15.469', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'approve_content', 'actor', 'email', true, 'f96a1ad9-1a44-11ea-8e80-11332eb0bcd9', '2019-12-09 11:00:13.28', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'approve_content', 'author', 'email', true, 'f993e9fa-1a44-11ea-8e80-172038b22a3f', '2019-12-09 11:00:13.554', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'reject_content', 'actor', 'email', true, 'fa8f60fe-1a44-11ea-8e80-89e6aac50254', '2019-12-09 11:00:15.202', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'delegate_content', 'delegatedActor', 'email', true, 'fc586137-1a44-11ea-8e80-2b58eae259ac', '2019-12-09 11:00:18.197', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'publish_content', 'author', 'email', true, 'fb64b262-1a44-11ea-8e80-add725b97350', '2019-12-09 11:00:16.6', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'share_goal', 'sharedBy', 'email', false, '28e091e4-1814-11ea-b44e-0b2d8a6a37c4', '2019-12-06 10:35:45.244', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'delegate_content', 'actor', 'email', true, 'fc308de6-1a44-11ea-8e80-4dfa508608ed', '2019-12-09 11:00:17.936', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'content_feedback_response', 'learner', 'email', true, '093b99a0-1d88-11ea-8f1d-5b27418fa0a9', '2019-12-13 09:07:49.588', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'ask_expert', 'learner', 'email', false, NULL, '2019-12-13 05:44:37.345', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'content_feedback', 'author', 'email', true, '098329f1-1d88-11ea-8f1d-b5fed3da43ef', '2019-12-13 09:07:50.057', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'ask_expert', 'expert', 'email', true, 'e17f2bcd-1d6d-11ea-8f1d-5597abea5279', '2019-12-13 06:00:36.006', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'content_feedback', 'learner', 'email', false, NULL, '2019-12-13 06:05:24.594', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'content_feedback_response', 'author', 'email', false, NULL, '2019-12-13 06:05:58.362', 'admin@eagle.com', NULL);
INSERT INTO wingspan.tenant_event_notification (root_org, org, event_id, recipient, mode, mode_activated, template_id, updated_on, updated_by, receiver_emails) VALUES ('igot', 'dopt', 'share_content', 'sharedBy', 'email', false, 'bad400f6-1814-11ea-b44e-c9d9df2bc5f0', '2019-12-06 10:39:50.109', 'admin@eagle.com', NULL);







INSERT INTO wingspan.event_parameters (root_org, org, event_id, parameter_id, value) VALUES ('igot', 'dopt', 'content_expiry_reminder', 'reminder_threshold', '28');



INSERT INTO wingspan.tbl_tenant_mode (root_org, org, mode, activated, icon_id, updated_on, updated_by) VALUES ('igot', 'dopt', 'email', true, '8910', current_timestamp, '');
INSERT INTO wingspan.tbl_tenant_mode (root_org, org, mode, activated, icon_id, updated_on, updated_by) VALUES ('igot', 'dopt', 'sms', false, '1234', current_timestamp, '');
INSERT INTO wingspan.tbl_tenant_mode (root_org, org, mode, activated, icon_id, updated_on, updated_by) VALUES ('igot', 'dopt', 'push', false, '4567', current_timestamp, '');






CREATE SCHEMA IF NOT EXISTS social;

ALTER SCHEMA social OWNER TO wingspan;

CREATE TABLE social.userpostactivity
(
    root_org character varying(32),
    org character varying(32),
    id uuid,
    post_id uuid NOT NULL,
    user_id uuid NOT NULL,
    isupvoted boolean,
    dtupvoted timestamp without time zone,
    isdownvoted boolean,
    dtdownvoted timestamp without time zone,
    isliked boolean,
    dtliked timestamp without time zone,
    isflagged boolean,
    dtflagged timestamp without time zone,
    flagcomment character varying(250),
    flagcommenttype character varying(50),
    dtdeleted timestamp without time zone,
    deletionreason character varying(250),
    dtreactivated timestamp without time zone,
    reactivationreason character varying(250),
    dtaccepted timestamp without time zone,
    dtrejected timestamp without time zone,
    rejectionreason character varying(250),
    CONSTRAINT pk_userpostactivity PRIMARY KEY (post_id, user_id),
    CONSTRAINT uk_userpostactivity UNIQUE (id)
);

ALTER TABLE social.userpostactivity
    OWNER to wingspan;
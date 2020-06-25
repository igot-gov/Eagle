-- common_learning_goals tables script starts


--common_learning_goals (parent table) creation

Create TABLE IF NOT EXISTS wingspan.common_learning_goals (
	root_org character varying,
	id character varying,
	version float,
	goal_content_id character varying,
	created_on TIMESTAMP,
	updated_on TIMESTAMP,
	created_by character varying,
	updated_by character varying,
	group_id character varying,
	CONSTRAINT clg_pkey PRIMARY KEY(root_org,id,version)
);

ALTER TABLE wingspan.common_learning_goals OWNER TO wingspan;

--common_learning_goals insert statements

--common_learning_goals_language (child table) creation

Create table IF NOT EXISTS wingspan.common_learning_goals_language(
	root_org character varying,
	id character varying,
	version float,
	language character varying,
	goal_desc character varying,
	goal_title character varying,
	group_name character varying,
	CONSTRAINT clg_lang_pkey PRIMARY KEY(root_org,id,version,language)
);							 			

ALTER TABLE wingspan.common_learning_goals_language OWNER TO wingspan;

--common_learning_goals_language insert statements


-- common_learning_goals tables script ends
CREATE TABLE IF NOT EXISTS wingspan.badge
(
  root_org text NOT NULL,
  badge_id text NOT NULL,
  image text NOT NULL,
  badge_type text NOT NULL,
  threshold1 numeric(10,2),
  time_period integer,
  threshold2 integer,
  created_date timestamp without time zone NOT NULL,
  created_by text NOT NULL,
  start_date timestamp without time zone,
  end_date timestamp without time zone,
  sharable text NOT NULL,
  badge_group text NOT NULL,
  group_order integer NOT NULL,
  CONSTRAINT badge_pkey PRIMARY KEY (root_org, badge_id)
);

ALTER TABLE wingspan.badge OWNER TO wingspan;

CREATE TABLE IF NOT EXISTS wingspan.badge_language
(
  root_org text NOT NULL,
  badge_id text NOT NULL,
  language text NOT NULL,
  badge_name text NOT NULL,
  badge_description text NOT NULL,
  completed_description text NOT NULL,
  message text NOT NULL,
  badge_group_text text,
  CONSTRAINT badge_language_pkey PRIMARY KEY (root_org, badge_id, language)
);

ALTER TABLE wingspan.badge_language OWNER TO wingspan;


/*************BADGE INSERT QUERIES******************/
INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'NewUser', '/content/Achievements/Badges/Fledgling.png?type=assets', 'O', NULL, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'User', 1);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Quiz1', '/content/Achievements/Badges/Duelist.png?type=assets', 'O', 1.00, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Quiz', 2);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Quiz100', '/content/Achievements/Badges/Genie.png?type=assets', 'O', 100.00, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Quiz', 2);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Quiz250', '/content/Achievements/Badges/Wizard.png?type=assets', 'O', 250.00, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Quiz', 2);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Quiz25', '/content/Achievements/Badges/Ace.png?type=assets', 'O', 25.00, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Quiz', 2);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Quiz1000', '/content/Achievements/Badges/Quizzard.png?type=assets', 'O', 1000.00, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Quiz', 2);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Course1', '/content/Achievements/Badges/Warrior.png?type=assets', 'O', 1.00, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Course', 3);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Course10', '/content/Achievements/Badges/Knight.png?type=assets', 'O', 10.00, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Course', 3);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Course25', '/content/Achievements/Badges/Champion.png?type=assets', 'O', 25.00, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Course', 3);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Course100', '/content/Achievements/Badges/Sensei.png?type=assets', 'O', 100.00, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Course', 3);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', '4Day', '/content/Achievements/Badges/Lightning Deer.png?type=assets', 'R', 4.00, 1, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Duration', 4);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', '20Week', '/content/Achievements/Badges/Power Tiger.png?type=assets', 'R', 20.00, 7, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Duration', 4);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Santa', '/content/Achievements/Badges/Santa.png?type=assets', 'O', NULL, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', '2018-12-01 00:00:00', '2018-12-31 23:59:59', 'no', 'User', 6);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', 'Elf', '/content/Achievements/Badges/Exploring Elf.png?type=assets', 'O', NULL, NULL, NULL, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', '2018-12-01 00:00:00', '2018-12-31 23:59:59', 'no', 'Learning Path', 6);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', '30MMonth', '/content/Achievements/Badges/Soaring Eagle.png?type=assets', 'R', 0.50, 30, 25, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Duration', 5);

INSERT INTO wingspan.badge (root_org, badge_id, image, badge_type, threshold1, time_period, threshold2, created_date, created_by, start_date, end_date, sharable, badge_group, group_order) VALUES ('root_org', '30MWeek', '/content/Achievements/Badges/Studious Bee.png?type=assets', 'R', 0.50, 7, 5, '2018-12-11 11:48:33', '5c7737a2-0712-4154-bb6a-2edac17036a9', NULL, NULL, 'no', 'Duration', 5);


/*******************BADGE LANGUAGE INSERTION QUERIES**************************/
INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'NewUser', 'en', 'The Fledgling', 'Register on Eagle platform', 'Registered on Eagle platform', 'You''ve got on to Eagle for learning. Good start!', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Quiz1', 'en', 'Duelist', 'Complete your first quiz resource', 'Completed first quiz', 'You have broken the rookie spell! Good job.', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Quiz25', 'en', 'Ace', 'Complete 25 quiz resources', 'Completed 25 quiz resources', '25 in hand and going strong. Congrats!', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Quiz100', 'en', 'Genie', 'Complete 100 quiz resources', 'Completed 100 quiz resources', 'You are a centurion! Congrats', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Quiz250', 'en', 'Wizard', 'Complete 250 quiz resources', 'Completed 250 quiz resources', 'You are a Quiz Whiz! There is no spell you can''t break. Great Job.', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Quiz1000', 'en', 'Quizzard', 'Complete 1000 quiz resources', 'Completed 1000 quiz resources', 'Riddle us this! You are a quizzing genius.', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Course1', 'en', 'Warrior', 'Complete your first course', 'Completed your first course', 'You took one giant leap towards your goal. Welcome to lifelong learning.', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Course10', 'en', 'Knight', 'Complete 10 courses', 'Completed 10 courses', 'You have surmounted the 10. Way to go!', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Course25', 'en', 'Champion', 'Complete 25 courses', 'Completed 25 courses', 'What a feat! Congrats.', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Course100', 'en', 'Sensei', 'Complete 100 courses', 'Completed 100 courses', 'You are the God of learning, we bow to you!', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', '4Day', 'en', 'Lightning Deer', 'Learn 4 hours a day', 'Learnt 4 hours a day', 'You are the lord of lightning! That was quick, do you want to continue learning?', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', '20Week', 'en', 'Power Tiger', 'Learn 20 hours in a week', 'Learnt 20 hours in a week', 'Your steadfastness is commendable. Don''t stop the learning.', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', '30MMonth', 'en', 'Soaring Eagle', 'Learn 30 minutes each day for 25 days in a month', 'Learnt 30 minutes each day for 25 days in a month', 'You have displayed the highest level of discipline and focus to achieve the ultimate badge.', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', '30MWeek', 'en', 'Studious Bee', 'Learn for 30 minutes each day for 5 days in a week', 'Learnt for 30 minutes each day for 5 days in a week', 'Discipline is the bridge between goals and accomplishment. Continue learning.', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Santa', 'en', 'Santa', 'Come to Eagle in December', 'Visited Eagle in December', 'Ho, Ho, Ho, Welcome to Eagle!', NULL);

INSERT INTO wingspan.badge_language (root_org, badge_id, language, badge_name, badge_description, completed_description, message, badge_group_text) VALUES ('root_org', 'Elf', 'en', 'Exploring Elf', 'Complete a Learning Path in December', 'Completed a Learning Path in December', 'Learn On! You''re on the right path of learning. Wanna continue?', NULL);
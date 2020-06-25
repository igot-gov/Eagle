/* CASSANDRA GOALS TABLES */

/* user_learning_goals  */
DROP INDEX idx_userlearninggoals;
DROP Materialized View bodhi.mv_user_goals_tracker;
DROP table bodhi.user_learning_goals;

/* user_shared_goals */
DROP INDEX  idx_usersharedgoalsstatus;
DROP Materialized View bodhi.mv_shared_goals_tracker;
DROP table bodhi.user_shared_goals;

/* CASSANDRA PLAYLIST TABLES */

/* user_playlist  */
DROP INDEX  id_userplaylist;
DROP table bodhi.user_playlist;

/* playlist_shared */
DROP table bodhi.playlist_shared;

/* playlist_recent */
DROP table bodhi.playlist_recent;

/* CASSANDRA BADGE TABLES */

DROP TABLE bodhi.user_badges;
DROP TABLE bodhi.badge;

/* Terms and Conditions */

DROP TABLE bodhi.user_terms_condition;
DROP TABLE bodhi.author_terms_condition;
DROP TABLE bodhi.terms_and_conditions;

/* CONTINUE LEARNING */

DROP MATERIALIZED VIEW bodhi.mv_continue_learning;
DROP TABLE bodhi.continue_learning;


/* Progress */
DROP MATERIALIZED VIEW bodhi.user_content_progress_by_date_updated;
DROP MATERIALIZED VIEW bodhi.user_content_progress_order_last_assess;
DROP MATERIALIZED VIEW bodhi.user_content_progress_by_last_accessed_on;
DROP TABLE bodhi.user_content_progress;

/* Points */
DROP TABLE bodhi.user_discrete_points; 
DROP TABLE bodhi.total_points;

/* Cohorts */
DROP TABLE bodhi.educators;

/* Leaderboard */
DROP MATERIALIZED VIEW bodhi.leaderboard_rank;
DROP TABLE bodhi.leaderboard;

/* User Assessment Master */
DROP MATERIALIZED VIEW bodhi.user_assessment_top_performer;
DROP MATERIALIZED VIEW bodhi.assessment_by_content_user;
DROP MATERIALIZED VIEW bodhi.user_assessment_by_date;
DROP TABLE bodhi.user_assessment_master;

/*--------------------------lnd exercises----------------------------*/
DROP TABLE bodhi.group_user_mapping;
DROP TABLE bodhi.educator_group_mapping;

/*--------------------------user_assessment_summary-----------------------*/
DROP TABLE bodhi.user_assessment_summary;

/* Quiz */
DROP TABLE bodhi.user_quiz_master;

/*  Quiz summary */
DROP TABLE bodhi.user_quiz_summary;

/*---------------------------user_exercise_last---------------------------*/
DROP MATERIALIZED VIEW bodhi.user_exercise_last_by_feedback;
DROP TABLE bodhi.user_exercise_last;

/*---------------------------user_exercise--------------------------------*/
DROP TABLE bodhi.user_exercise;

/*---------------------------live_events--------------------------------*/
DROP TABLE bodhi.live_events;

/*  user_roles  */
DROP INDEX user_roles_roles_idx;
DROP TABLE bodhi.user_roles;

/*  user_likes  */
DROP TABLE bodhi.user_likes;

/*  preferences  */
DROP TABLE bodhi.user_preferences;


/* ratings */
DROP TABLE bodhi.user_content_rating;
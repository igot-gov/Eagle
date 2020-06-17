CREATE TABLE IF NOT EXISTS bodhi.master_values (
	root_org text,
	entity text,
	date_modified timestamp,
	values LIST<text>,
	PRIMARY KEY (root_org,entity)
);


INSERT INTO master_values_v2 (root_org,entity,date_modified,strignify,values) VALUES ('eagle','audience',1585917837698,false,['All', 'Developers', 'Managers', 'Sales', 'Onboarding']);
INSERT INTO master_values_v2 (root_org,entity,date_modified,strignify,values) VALUES ('eagle','complexityLevel',1585917837698,false,['Beginner', 'Intermediate', 'Advanced', 'Expert']);
INSERT INTO master_values_v2 (root_org,entity,date_modified,strignify,values) VALUES ('eagle','categoryType',1585917837698,false,['Article', 'Lecture', 'Quiz', 'Exercise', 'Prelude', 'Tryout', 'Assessment', 'Offering', 'Webinar-Recording', 'Case-Study', 'Insights', 'How-To', 'Brochure', 'Lesson', 'Research-Study', 'Livestream', 'Elevator-Pitch', 'Primer', 'Capstone-Project', 'Client-Deck', 'Podcast', 'Body-of-Knowledge', 'Code-Component', 'Analyst-Report', 'Point-of-View', 'Infographics', 'HandsOnAssessment', 'Competitive-Insights', 'References', 'Concepts', 'Facts', 'Processes', 'Procedures', 'Principles']);
INSERT INTO master_values_v2 (root_org,entity,date_modified,strignify,values) VALUES ('eagle','learningMode',1585917837698,false,['Self-Paced','Instructor-Led']);

INSERT INTO master_values_v2 (root_org,entity,date_modified,strignify,values) VALUES ('eagle','resourceType',1585917837698,false,['Article', 'Lecture', 'Quiz', 'Exercise', 'Prelude', 'Tryout', 'Assessment', 'Offering', 'Webinar-Recording', 'Case-Study', 'Insights', 'How-To', 'Brochure', 'Lesson', 'Research-Study', 'Livestream', 'Elevator-Pitch', 'Primer', 'Capstone-Project', 'Client-Deck', 'Podcast', 'Body-of-Knowledge', 'Code-Component', 'Analyst-Report', 'Point-of-View', 'Infographics', 'HandsOnAssessment', 'Competitive-Insights', 'References', 'Concepts', 'Facts', 'Processes', 'Procedures', 'Principles']);
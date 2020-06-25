
CREATE TABLE bodhi.api_authentication (
    key text PRIMARY KEY,
    name text,
    urls list<text>,
    value text
);

insert into api_authentication(key, name, urls, value) values ('1002','learninganalytics',['/la/userinterests', '/la/badges', '/la/leaderboard', '/la/usertnc', '/lex/userProgress', '/la/sharedplaylist', '/la/userplaylist', '/la/commonlearninggoals', '/la/userassessments', '/la/userlearninggoals', '/la/usersharedgoals', '/la/catalog', '/la/feedback', '/la/tags', '/lex/badges', '/lex/emailFromUserId', '/la/masterinterests', '/la/masterinterests', '/la/social/posts', '/api/download'],'7053b6d56fdb18edbf04fe3076647a67');
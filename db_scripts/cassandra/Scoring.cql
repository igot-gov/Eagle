
CREATE TABLE IF NOT EXISTS bodhi.score_template (
    root_org text,
    org text, 
    template_id text,
    version int,
    weightage float,
    max_score float,
    min_acceptable_score float,
    PRIMARY KEY (root_org, org, template_id)
);



CREATE TABLE IF NOT EXISTS bodhi.evaluation_criteria (
    root_org text,
    org text, 
    ref_template_id text,
    criteria_id text,
    criteria text,
    weightage float,
    max_score float,
    min_acceptable_score float,
    PRIMARY KEY (root_org, org, criteria)
);
INSERT INTO bodhi.evaluation_criteria json '{"root_org": "igot", "org": "dopt", "ref_template_id":"temp-01","criteria_id":"cri-001","criteria":"Instructional Methods", "weightage":0.3, "max_score": 45, "min_acceptable_score":33.75}';

CREATE TABLE IF NOT EXISTS bodhi.score_qualifier (
    root_org text,
    org text,
    ref_criteria_id text,
    qualifier text,
    fixed_score map<text,int>,
    range_score map<text,text>,
    PRIMARY KEY (root_org, org, ref_criteria_id, qualifier)
);

INSERT INTO bodhi.score_qualifier json '{"root_org": "igot", "org": "dopt", "ref_criteria_id":"cri-001",
"qualifier": "The course puts the learner at the centre of the learning experience." , 
"fixed_score": {"StronglyAgree": 5, "Agree":3, "Disagree":1, "Strongly Disagree":0}, 
"range_score": {} }';


INSERT INTO bodhi.score_qualifier json '{"root_org": "igot", "org": "dopt", "ref_criteria_id":"cri-001",
"qualifier": "Passing criteria is clearly stated." , 
"fixed_score": {"StronglyAgree": 5, "Agree":3, "Disagree":1, "Strongly Disagree":0}, 
"range_score": {} }';



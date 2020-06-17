insert into app_config(root_org,key,remarks,value) values ('default','page','mimeType for which progress should calculated based on page visit','application/web-module,application/pdf,application/html,application/rdbms,application/htmlpicker,application/integrated-hands-on');
insert into app_config(root_org,key,remarks,value) values ('default','result','mimeType for which progress should calculated based on evaluation of submission','application/quiz,application/iap-assessment,resource/collection,application/web-module-exercise,backend/submission,application/drag-drop,application/class-diagram');
insert into app_config(root_org,key,remarks,value) values ('default','time','mimeType for which progress should calculated based on time stamp','video/interactive,video/mp4,video/x-youtube,audio/mpeg,application/x-mpegurl,audio/mp3,audio/wav');
insert into app_config(root_org,key,remarks,value) values ('default','allowedlanguages','','ar,br,cz,da,de,el,en,es,fr,fr_ca,hr,hu,id,it,ja,ko,nl,no,pl,pt,ru,sl,sv,th,tr,zh');

insert into app_config(root_org,key,remarks,value) values ('root_org','feedbackSentimentMode','This is to tell if sentiment will be taken by user or not while giving feedback','true');
insert into app_config(root_org,key,remarks,value) values ('root_org','feedbackSentimentMode','This is to tell if sentiment will be taken by user or not while giving feedback','true');
insert into app_config(root_org,key,remarks,value) values ('root_org','content_request_active','This indicates if service request can be given by user or not','true');

insert into app_config(root_org,key,remarks,value) values ('root_org','feedbackCategories','This are Feefback categories for FeedBack Tags UI','category 1,category 2,category 3,category 4,category 5,category 6,category 7,category 8');
insert into app_config(root_org,key,remarks,value) values ('root_org','mac_for_external','mark as complete allowed for external content','true');
insert into app_config(root_org,key,remarks,value) values ('root_org','mail_name','The application name to be changed on Notification Template','LEX');
insert into app_config(root_org,key,remarks,value) values ('root_org','service_request_active','This indicates if service request can be given by user or not','false');
insert into app_config(root_org,key,remarks,value) values ('root_org','invalid_domains','domains to which emails are not allowed','demo.com');
insert into app_config(root_org,key,remarks,value) values ('root_org','mail_id','The email id to be changed on Notification Template','support@root_org.gov.in');
insert into app_config(root_org,key,remarks,value) values ('root_org','access_token','The Microsoft access token','');
insert into app_config(root_org,key,remarks,value) values ('root_org','user_data_source','This is for root_org user data source','PID');
insert into app_config(root_org,key,remarks,value) values ('root_org','token_expires_on','The time stamp on which Microsoft access token expires on ','');
insert into app_config(root_org,key,remarks,value) values ('root_org','default_user_access_path','','root_org,root_org/org');
insert into app_config(root_org,key,remarks,value) values ('root_org','mac_for_instructor_led','mark as complete allowed for instructor-led content','false');
insert into app_config(root_org,key,remarks,value) values ('root_org','nsoDataUrl','','/web-hosted/navigator/json/nsodata.json');
insert into app_config(root_org,key,remarks,value) values ('root_org','feedbackAllowSelfReply','This indicates user can Self Reply on the Feedback Provided on not','true');
insert into app_config(root_org,key,remarks,value) values ('root_org','email_validate_options','The sources to validate emailIds','user,graph');
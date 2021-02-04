CREATE TABLE if not exists bodhi.work_flow (
   root_org text, 
   org text, 
   service text, 
   config text, 
   PRIMARY KEY (root_org, org, service));


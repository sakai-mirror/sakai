-- This is the MySQL Sakai 2.1.0 -> 2.1.1 conversion script
----------------------------------------------------------------------------------------------------------------------------------------
--
-- use this to convert a Sakai database from 2.1.0 to 2.1.1.  Run this before you run your first app server.
-- auto.ddl does not need to be enabled in your app server - this script takes care of all new TABLEs, changed TABLEs, and changed data.
--
----------------------------------------------------------------------------------------------------------------------------------------


-- Gradebook
ALTER TABLE GB_GRADE_RECORD_T ADD CONSTRAINT UNIQUE (GRADABLE_OBJECT_ID, STUDENT_ID);

-- RWiki
create table rwikipagetrigger (id varchar(36) not null, userid varchar(64) not null, pagespace varchar(255), pagename varchar(255), lastseen datetime, triggerspec text, primary key (id));
create table rwikipagemessage (id varchar(36) not null, sessionid varchar(255), userid varchar(64) not null, pagespace varchar(255), pagename varchar(255), lastseen datetime, message text, primary key (id));
create table rwikipagepresence (id varchar(36) not null, sessionid varchar(255), userid varchar(64) not null, pagespace varchar(255), pagename varchar(255), lastseen datetime, primary key (id));
create table rwikipreference (id varchar(36) not null, userid varchar(64) not null, lastseen datetime, preference text, primary key (id));

-- SAM
ALTER TABLE SAM_PUBLISHEDFEEDBACK_T ADD COLUMN FEEDBACKAUTHORING integer;
ALTER TABLE SAM_ASSESSFEEDBACK_T ADD COLUMN FEEDBACKAUTHORING integer;
UPDATE SAM_ASSESSFEEDBACK_T SET FEEDBACKAUTHORING = 1 WHERE ASSESSMENTID = 1;



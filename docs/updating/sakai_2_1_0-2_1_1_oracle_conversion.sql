-- This is the Oracle Sakai 2.1.0 -> 2.1.1 conversion script
----------------------------------------------------------------------------------------------------------------------------------------
--
-- use this to convert a Sakai database from 2.1.0 to 2.1.1.  Run this before you run your first app server.
-- auto.ddl does not need to be enabled in your app server - this script takes care of all new TABLEs, changed TABLEs, and changed data.
--
----------------------------------------------------------------------------------------------------------------------------------------


-- Gradebook
ALTER TABLE GB_GRADE_RECORD_T ADD UNIQUE (GRADABLE_OBJECT_ID, STUDENT_ID);

-- RWiki
create table rwikipagetrigger (id varchar2(36) not null, userid varchar2(64) not null, pagespace varchar2(255), pagename varchar2(255), lastseen date, triggerspec clob, primary key (id));
create table rwikipagemessage (id varchar2(36) not null, sessionid varchar2(255), userid varchar2(64) not null, pagespace varchar2(255), pagename varchar2(255), lastseen date, message clob, primary key (id));
create table rwikipagepresence (id varchar2(36) not null, sessionid varchar2(255), userid varchar2(64) not null, pagespace varchar2(255), pagename varchar2(255), lastseen date, primary key (id));
create table rwikipreference (id varchar2(36) not null, userid varchar2(64) not null, lastseen date, preference clob, primary key (id));


-- SAM
ALTER TABLE SAM_PUBLISHEDFEEDBACK_T ADD FEEDBACKAUTHORING integer;
ALTER TABLE SAM_ASSESSFEEDBACK_T ADD FEEDBACKAUTHORING integer;
UPDATE SAM_ASSESSFEEDBACK_T SET FEEDBACKAUTHORING = 1 WHERE ASSESSMENTID = 1;



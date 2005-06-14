-----------------------------------------------------------------------------
-- CHEF_EVENT
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS CHEF_EVENT;

CREATE TABLE CHEF_EVENT
(
	EVENT_ID INT,
	EVENT_DATE DATE,
	EVENT VARCHAR (32),
	REF VARCHAR (256),
	SESSION_ID VARCHAR (32),
	EVENT_CODE VARCHAR (1),
	PRIMARY KEY (EVENT_ID)
);

CREATE SEQUENCE CHEF_EVENT_SEQ;

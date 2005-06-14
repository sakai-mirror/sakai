-----------------------------------------------------------------------------
-- CHEF_EVENT
-----------------------------------------------------------------------------

DROP TABLE CHEF_EVENT;

CREATE TABLE CHEF_EVENT
(
	EVENT_ID NUMBER,
	EVENT_DATE DATE,
	EVENT VARCHAR2 (32),
	REF VARCHAR2 (256),
	SESSION_ID VARCHAR2 (32),
	EVENT_CODE VARCHAR2 (1)
);

CREATE UNIQUE INDEX CHEF_EVENT_INDEX ON CHEF_EVENT
(
	EVENT_ID
);

DROP SEQUENCE CHEF_EVENT_SEQ;

CREATE SEQUENCE CHEF_EVENT_SEQ;

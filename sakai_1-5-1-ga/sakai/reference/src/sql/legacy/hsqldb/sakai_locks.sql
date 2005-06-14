-----------------------------------------------------------------------------
-- SAKAI_LOCKS
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_LOCKS;

CREATE TABLE SAKAI_LOCKS
(
	TABLE_NAME VARCHAR (64),
	RECORD_ID VARCHAR (512),
	LOCK_TIME DATETIME,
	USAGE_SESSION_ID VARCHAR (32),
	CONSTRAINT SAKAI_LOCKS_INDEX UNIQUE (TABLE_NAME, RECORD_ID)
);


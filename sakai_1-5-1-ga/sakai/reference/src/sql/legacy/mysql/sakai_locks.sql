-----------------------------------------------------------------------------
-- SAKAI_LOCKS
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_LOCKS;

CREATE TABLE SAKAI_LOCKS
(
	TABLE_NAME VARCHAR (64),
	RECORD_ID VARCHAR (512),
	LOCK_TIME DATETIME,
	USAGE_SESSION_ID VARCHAR (32)
);

CREATE UNIQUE INDEX SAKAI_LOCKS_INDEX ON SAKAI_LOCKS
(
	TABLE_NAME(64), RECORD_ID(128)
);


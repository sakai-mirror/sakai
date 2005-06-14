-----------------------------------------------------------------------------
-- DISSERTATION_DISSERTATION
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS DISSERTATION_DISSERTATION;

CREATE TABLE DISSERTATION_DISSERTATION
(
    DISSERTATION_ID VARCHAR (99) NOT NULL,
    XML LONG
);

CREATE UNIQUE INDEX DISSERTATION_DISS_INDEX ON DISSERTATION_DISSERTATION
(
	DISSERTATION_ID
);

-----------------------------------------------------------------------------
-- DISSERTATION_GROUP
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS DISSERTATION_GROUP;

CREATE TABLE DISSERTATION_GROUP
(
    GROUP_ID VARCHAR (99) NOT NULL,
    XML LONG
);

CREATE UNIQUE INDEX DISSERTATION_GROUP_INDEX ON DISSERTATION_GROUP
(
	GROUP_ID
);

-----------------------------------------------------------------------------
-- DISSERTATION_STEP
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS DISSERTATION_STEP;

CREATE TABLE DISSERTATION_STEP
(
    STEP_ID VARCHAR (99) NOT NULL,
    XML LONG
);

CREATE UNIQUE INDEX DISSERTATION_STEP_INDEX ON DISSERTATION_STEP
(
	STEP_ID
);

-----------------------------------------------------------------------------
-- DISSERTATION_PATH
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS DISSERTATION_PATH;

CREATE TABLE DISSERTATION_PATH
(
    PATH_ID VARCHAR (99) NOT NULL,
    XML LONG
);

CREATE UNIQUE INDEX DISSERTATION_PATH_INDEX ON DISSERTATION_PATH
(
	PATH_ID
);

-----------------------------------------------------------------------------
-- DISSERTATION_STATUS
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS DISSERTATION_STATUS;

CREATE TABLE DISSERTATION_STATUS
(
    STATUS_ID VARCHAR (99) NOT NULL,
    XML LONG
);

CREATE UNIQUE INDEX DISSERTATION_STATUS_INDEX ON DISSERTATION_STATUS
(
	STATUS_ID
);

-----------------------------------------------------------------------------
-- DISSERTATION_CANDIDATEINFO
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS DISSERTATION_CANDIDATEINFO;

CREATE TABLE DISSERTATION_CANDIDATEINFO
(
    INFO_ID VARCHAR (99) NOT NULL,
    XML LONG
);

CREATE UNIQUE INDEX DISSERTATION_INFO_INDEX ON DISSERTATION_CANDIDATEINFO
(
	INFO_ID
);

DESCRIBE DISSERTATION_DISSERTATION;
DESCRIBE DISSERTATION_GROUP;
DESCRIBE DISSERTATION_STEP;
DESCRIBE DISSERTATION_PATH;
DESCRIBE DISSERTATION_STATUS;
DESCRIBE DISSERTATION_CANDIDATEINFO;



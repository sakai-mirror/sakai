-----------------------------------------------------------------------------
-- ASSIGNMENT_ASSIGNMENT
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS ASSIGNMENT_ASSIGNMENT;

CREATE TABLE ASSIGNMENT_ASSIGNMENT
(
    ASSIGNMENT_ID VARCHAR (99) NOT NULL,
	CONTEXT VARCHAR (99),
    XML LONGTEXT
);

CREATE UNIQUE INDEX ASSIGNMENT_ASSIGNMENT_INDEX ON ASSIGNMENT_ASSIGNMENT
(
	ASSIGNMENT_ID
);

CREATE INDEX ASSIGNMENT_ASSIGNMENT_CONTEXT ON ASSIGNMENT_ASSIGNMENT
(
	CONTEXT
);

-----------------------------------------------------------------------------
-- ASSIGNMENT_CONTENT
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS ASSIGNMENT_CONTENT;

CREATE TABLE ASSIGNMENT_CONTENT
(
    CONTENT_ID VARCHAR (99) NOT NULL,
	CONTEXT VARCHAR (99),
    XML LONGTEXT
);

CREATE UNIQUE INDEX ASSIGNMENT_CONTENT_INDEX ON ASSIGNMENT_CONTENT
(
	CONTENT_ID
);

CREATE INDEX ASSIGNMENT_CONTENT_CONTEXT ON ASSIGNMENT_CONTENT
(
	CONTEXT
);

-----------------------------------------------------------------------------
-- ASSIGNMENT_SUBMISSION
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS ASSIGNMENT_SUBMISSION;

CREATE TABLE ASSIGNMENT_SUBMISSION
(
    SUBMISSION_ID VARCHAR (99) NOT NULL,
	CONTEXT VARCHAR (99),
    XML LONGTEXT
);

CREATE UNIQUE INDEX ASSIGNMENT_SUBMISSION_INDEX ON ASSIGNMENT_SUBMISSION
(
	SUBMISSION_ID
);

CREATE INDEX ASSIGNMENT_SUBMISSION_CONTEXT ON ASSIGNMENT_SUBMISSION
(
	CONTEXT
);

DESCRIBE ASSIGNMENT_ASSIGNMENT;
DESCRIBE ASSIGNMENT_CONTENT;
DESCRIBE ASSIGNMENT_SUBMISSION;

-- to convert from pre Sakai 2.0.5 (also add the indexes)
--ALTER TABLE ASSIGNMENT_ASSIGNMENT ADD CONTEXT VARCHAR2 (99);
--ALTER TABLE ASSIGNMENT_CONTENT ADD CONTEXT VARCHAR2 (99);
--ALTER TABLE ASSIGNMENT_SUBMISSION ADD CONTEXT VARCHAR2 (99);



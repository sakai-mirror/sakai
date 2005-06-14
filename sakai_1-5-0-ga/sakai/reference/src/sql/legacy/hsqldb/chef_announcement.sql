-----------------------------------------------------------------------------
-- ANNOUNCEMENT_CHANNEL
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS ANNOUNCEMENT_CHANNEL;

CREATE TABLE ANNOUNCEMENT_CHANNEL
(
    CHANNEL_ID VARCHAR (99) NOT NULL,
	NEXT_ID INT,
    XML LONGVARCHAR,
    CONSTRAINT ANNOUNCEMENT_CHANNEL_INDEX UNIQUE (CHANNEL_ID)
);

-----------------------------------------------------------------------------
-- ANNOUNCEMENT_MESSAGE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS ANNOUNCEMENT_MESSAGE;

CREATE TABLE ANNOUNCEMENT_MESSAGE (
       CHANNEL_ID           VARCHAR(99) NOT NULL,
       MESSAGE_ID           VARCHAR(32) NOT NULL,
       DRAFT                CHAR(1) NULL,
 --                                  CHECK (DRAFT IN (1, 0)),
       PUBVIEW              CHAR(1) NULL,
 --                                  CHECK (PUBVIEW IN (1, 0)),
       OWNER                VARCHAR (99) NULL,
       MESSAGE_DATE         DATETIME NOT NULL,
       XML                  LONGVARCHAR NULL,
       PRIMARY KEY (CHANNEL_ID, MESSAGE_ID)
);


DROP INDEX IE_ANNC_MSG_CHANNEL IF EXISTS;
DROP INDEX IE_ANNC_MSG_ATTRIB IF EXISTS;
DROP INDEX IE_ANNC_MSG_DATE  IF EXISTS;
DROP INDEX IE_ANNC_MSG_DATE_DESC IF EXISTS;

CREATE INDEX IE_ANNC_MSG_CHANNEL ON ANNOUNCEMENT_MESSAGE
(
       CHANNEL_ID                     ASC
);

CREATE INDEX IE_ANNC_MSG_ATTRIB ON ANNOUNCEMENT_MESSAGE
(
       DRAFT                          ASC,
       PUBVIEW                        ASC,
       OWNER                          ASC
);

CREATE INDEX IE_ANNC_MSG_DATE ON ANNOUNCEMENT_MESSAGE
(
       MESSAGE_DATE                   ASC
);

CREATE INDEX IE_ANNC_MSG_DATE_DESC ON ANNOUNCEMENT_MESSAGE
(
       MESSAGE_DATE                   DESC
);

CREATE INDEX ANNOUNCEMENT_MESSAGE_CDD ON ANNOUNCEMENT_MESSAGE
(
	CHANNEL_ID,
	MESSAGE_DATE,
	DRAFT
);

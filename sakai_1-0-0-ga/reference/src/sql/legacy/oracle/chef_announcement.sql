-----------------------------------------------------------------------------
-- ANNOUNCEMENT_CHANNEL
-----------------------------------------------------------------------------

DROP TABLE ANNOUNCEMENT_CHANNEL CASCADE CONSTRAINTS;

CREATE TABLE ANNOUNCEMENT_CHANNEL
(
    CHANNEL_ID VARCHAR2 (99) NOT NULL,
	NEXT_ID INT,
    XML LONG
);

CREATE UNIQUE INDEX ANNOUNCEMENT_CHANNEL_INDEX ON ANNOUNCEMENT_CHANNEL
(
	CHANNEL_ID
);

-----------------------------------------------------------------------------
-- ANNOUNCEMENT_MESSAGE
-----------------------------------------------------------------------------

DROP TABLE ANNOUNCEMENT_MESSAGE CASCADE CONSTRAINTS;

CREATE TABLE ANNOUNCEMENT_MESSAGE (
       CHANNEL_ID           VARCHAR2(99 BYTE) NOT NULL,
       MESSAGE_ID           VARCHAR2(32 BYTE) NOT NULL,
       DRAFT                CHAR(1) NULL
                                   CHECK (DRAFT IN (1, 0)),
       PUBVIEW              CHAR(1) NULL
                                   CHECK (PUBVIEW IN (1, 0)),
       OWNER                VARCHAR2(99) NULL,
       MESSAGE_DATE         DATE NOT NULL,
       XML                  LONG NULL
);


ALTER TABLE ANNOUNCEMENT_MESSAGE
       ADD  ( PRIMARY KEY (CHANNEL_ID, MESSAGE_ID) ) ;

DROP INDEX XIF1ANNOUNCEMENT_MESSAGE;

DROP INDEX IE_ANNC_MSG_CHANNEL;

DROP INDEX IE_ANNC_MSG_ATTRIB;

DROP INDEX IE_ANNC_MSG_DATE;

DROP INDEX IE_ANNC_MSG_DATE_DESC;

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


-- to convert from pre Sakai 2.0.6 (also add the indexes)
--ALTER TABLE ANNOUNCEMENT_MESSAGE ADD DRAFT CHAR (1);
--ALTER TABLE ANNOUNCEMENT_MESSAGE ADD PUBVIEW CHAR (1);
--ALTER TABLE ANNOUNCEMENT_MESSAGE ADD OWNER VARCHAR2 (99);


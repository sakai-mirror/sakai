-----------------------------------------------------------------------------
-- MAILARCHIVE_CHANNEL
-----------------------------------------------------------------------------

DROP TABLE MAILARCHIVE_CHANNEL CASCADE CONSTRAINTS;

CREATE TABLE MAILARCHIVE_CHANNEL
(
    CHANNEL_ID VARCHAR2 (99) NOT NULL,
	NEXT_ID INT,
    XML LONG
);

CREATE UNIQUE INDEX MAILARCHIVE_CHANNEL_INDEX ON MAILARCHIVE_CHANNEL
(
	CHANNEL_ID
);

-----------------------------------------------------------------------------
-- MAILARCHIVE_MESSAGE
-----------------------------------------------------------------------------

DROP TABLE MAILARCHIVE_MESSAGE CASCADE CONSTRAINTS;

CREATE TABLE MAILARCHIVE_MESSAGE (
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

ALTER TABLE MAILARCHIVE_MESSAGE
       ADD  ( PRIMARY KEY (CHANNEL_ID, MESSAGE_ID) ) ;

DROP INDEX XIF1MAILARCHIVE_MESSAGE;

DROP INDEX IE_MAILARC_MSG_CHAN;

DROP INDEX IE_MAILARC_MSG_ATTRIB;

DROP INDEX IE_MAILARC_MSG_DATE;

DROP INDEX IE_MAILARC_MSG_DATE_DESC;

CREATE INDEX IE_MAILARC_MSG_CHAN ON MAILARCHIVE_MESSAGE
(
       CHANNEL_ID                     ASC
);

CREATE INDEX IE_MAILARC_MSG_ATTRIB ON MAILARCHIVE_MESSAGE
(
       DRAFT                          ASC,
       PUBVIEW                        ASC,
       OWNER                          ASC
);

CREATE INDEX IE_MAILARC_MSG_DATE ON MAILARCHIVE_MESSAGE
(
       MESSAGE_DATE                   ASC
);

CREATE INDEX IE_MAILARC_MSG_DATE_DESC ON MAILARCHIVE_MESSAGE
(
       MESSAGE_DATE                   DESC
);


-- to convert from pre Sakai 2.0.6 (also add the indexes)
--ALTER TABLE MAILARCHIVE_MESSAGE ADD DRAFT CHAR (1);
--ALTER TABLE MAILARCHIVE_MESSAGE ADD PUBVIEW CHAR (1);
--ALTER TABLE MAILARCHIVE_MESSAGE ADD OWNER VARCHAR2 (99);


INSERT INTO MAILARCHIVE_CHANNEL VALUES ('/mailarchive/channel/!site/postmaster', 1, '<?xml version="1.0" encoding="UTF-8"?>
<channel context="!site" id="postmaster" next-message-id="1">
	<properties/>
</channel>
');

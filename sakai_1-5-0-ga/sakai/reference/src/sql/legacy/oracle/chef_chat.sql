-----------------------------------------------------------------------------
-- CHAT_CHANNEL
-----------------------------------------------------------------------------

DROP TABLE CHAT_CHANNEL CASCADE CONSTRAINTS;

CREATE TABLE CHAT_CHANNEL
(
    CHANNEL_ID VARCHAR2 (99) NOT NULL,
	NEXT_ID INT,
    XML LONG
);

CREATE UNIQUE INDEX CHAT_CHANNEL_INDEX ON CHAT_CHANNEL
(
	CHANNEL_ID
);

-----------------------------------------------------------------------------
-- CHAT_MESSAGE
-----------------------------------------------------------------------------

DROP TABLE CHAT_MESSAGE CASCADE CONSTRAINTS;

CREATE TABLE CHAT_MESSAGE (
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

ALTER TABLE CHAT_MESSAGE
       ADD  ( PRIMARY KEY (CHANNEL_ID, MESSAGE_ID) ) ;

DROP INDEX XIF1CHAT_MESSAGE;

DROP INDEX IE_CHAT_MESSAGE_CHANNEL;

DROP INDEX IE_CHAT_MESSAGE_ATTRIB;

DROP INDEX IE_CHAT_MESSAGE_DATE;

DROP INDEX IE_CHAT_MESSAGE_DATE_DESC;

DROP INDEX CHAT_MSG_CDD;

CREATE INDEX IE_CHAT_MESSAGE_CHANNEL ON CHAT_MESSAGE
(
       CHANNEL_ID                     ASC
);

CREATE INDEX IE_CHAT_MESSAGE_ATTRIB ON CHAT_MESSAGE
(
       DRAFT                          ASC,
       PUBVIEW                        ASC,
       OWNER                          ASC
);

CREATE INDEX IE_CHAT_MESSAGE_DATE ON CHAT_MESSAGE
(
       MESSAGE_DATE                   ASC
);

CREATE INDEX IE_CHAT_MESSAGE_DATE_DESC ON CHAT_MESSAGE
(
       MESSAGE_DATE                   DESC
);

CREATE INDEX CHAT_MSG_CDD ON CHAT_MESSAGE
(
	CHANNEL_ID,
	MESSAGE_DATE,
	DRAFT
);


-- to convert from pre Sakai 2.0.6 (also add the indexes)
--ALTER TABLE CHAT_MESSAGE ADD DRAFT CHAR (1);
--ALTER TABLE CHAT_MESSAGE ADD PUBVIEW CHAR (1);
--ALTER TABLE CHAT_MESSAGE ADD OWNER VARCHAR2 (99);

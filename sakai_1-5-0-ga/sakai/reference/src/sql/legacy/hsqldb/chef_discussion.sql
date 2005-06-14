-----------------------------------------------------------------------------
-- DISCUSSION_CHANNEL
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS DISCUSSION_MESSAGE;
DROP TABLE IF EXISTS DISCUSSION_CHANNEL;

CREATE TABLE DISCUSSION_CHANNEL
(
    CHANNEL_ID VARCHAR (99) NOT NULL,
	NEXT_ID INT,
    XML LONGVARCHAR,
    CONSTRAINT DISCUSSION_CHANNEL_INDEX UNIQUE (CHANNEL_ID)
);

-----------------------------------------------------------------------------
-- DISCUSSION_MESSAGE
-- Note that this design does not enforce referential integrity
-- between channels and messages.  Upon deletion of a DISCUSSION_CHANNEL,
-- the foreign key constraints will merely set the discussion_message keys 
-- to NULL, rather than delete the DISCUSSION_MESSAGE record.  This will
-- leave orphans.  Additionally, the REPLY constraint is a fishhook 
-- relationship back to DISCUSSION_MESSAGE, which will also set the REPLY
-- field to NULL on deletion of a record.  This could leave orphaned 
-- messages that do not contain references back to their parent, which 
-- may or may not be acceptable, depending upon the design of the 
-- application.
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS DISCUSSION_MESSAGE;

CREATE TABLE DISCUSSION_MESSAGE (
       MESSAGE_ID           VARCHAR (32) NOT NULL,
       CHANNEL_ID           VARCHAR (99) NULL,
       DRAFT                CHAR(1) NULL,
--                                   CHECK (DRAFT IN (1, 0)),
       PUBVIEW              CHAR(1) NULL,
--                                   CHECK (PUBVIEW IN (1, 0)),
       OWNER                VARCHAR (99) NULL,
       CATEGORY             VARCHAR (255) NULL,
       REPLY                VARCHAR (32) NULL,
       MESSAGE_DATE         DATETIME NOT NULL,
       XML                  LONGVARCHAR NULL,
       PRIMARY KEY (MESSAGE_ID)
);

CREATE INDEX FK_DISC_MSG_CHANNEL ON DISCUSSION_MESSAGE
(
       CHANNEL_ID                     ASC
);

CREATE INDEX IE_DISC_MSG_ATTRIB ON DISCUSSION_MESSAGE
(
       DRAFT                          ASC,
       PUBVIEW                        ASC,
       OWNER                          ASC
);

CREATE INDEX IE_DISC_MSG_DATE ON DISCUSSION_MESSAGE
(
       MESSAGE_DATE                   ASC
);

CREATE INDEX IE_DISC_MSG_DATE_DESC ON DISCUSSION_MESSAGE
(
       MESSAGE_DATE                   DESC
);

CREATE INDEX IE_DISC_MSG_CATEGORY ON DISCUSSION_MESSAGE
(
       CATEGORY                       ASC
);

CREATE INDEX IE_DISC_MSG_REPLY ON DISCUSSION_MESSAGE
(
       REPLY                          ASC
);

CREATE INDEX DISC_MSG_CDD ON DISCUSSION_MESSAGE
(
	CHANNEL_ID,
	MESSAGE_DATE,
	DRAFT
);

ALTER TABLE DISCUSSION_MESSAGE
       ADD FOREIGN KEY (REPLY)
                             REFERENCES DISCUSSION_MESSAGE (MESSAGE_ID)
                             ON DELETE SET NULL;

ALTER TABLE DISCUSSION_MESSAGE
       ADD FOREIGN KEY (CHANNEL_ID)
                             REFERENCES DISCUSSION_CHANNEL (CHANNEL_ID)
                             ON DELETE SET NULL;



-----------------------------------------------------------------------------
-- SAKAI_USER_PROPERTY
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_USER_PROPERTY;

CREATE TABLE SAKAI_USER_PROPERTY (
       USER_ID             VARCHAR (99) NOT NULL,
       NAME                 VARCHAR (99) NOT NULL,
       VALUE                LONGVARCHAR NULL,
       PRIMARY KEY (USER_ID, NAME)
);

-----------------------------------------------------------------------------
-- SAKAI_USER
-----------------------------------------------------------------------------


DROP TABLE IF EXISTS SAKAI_USER;

CREATE TABLE SAKAI_USER (
       USER_ID              VARCHAR (99) NOT NULL,
       EMAIL                VARCHAR (255) NULL,
       FIRST_NAME           VARCHAR (255) NULL,
       LAST_NAME            VARCHAR (255) NULL,
       TYPE                 VARCHAR (255) NULL,
       PW                   VARCHAR (255) NULL,
       CREATEDBY            VARCHAR (99) NOT NULL,
       MODIFIEDBY           VARCHAR (99) NOT NULL,
       CREATEDON            TIMESTAMP NOT NULL,
       MODIFIEDON           DATETIME NOT NULL,
       PRIMARY KEY (USER_ID)
);

ALTER TABLE SAKAI_USER_PROPERTY
       ADD FOREIGN KEY (USER_ID)
                             REFERENCES SAKAI_USER (USER_ID);

CREATE INDEX IE_SAKAI_USER_CREATED ON SAKAI_USER
(
       CREATEDBY                      ASC,
       CREATEDON                      ASC
);

CREATE INDEX IE_SAKAI_USER_MODDED ON SAKAI_USER
(
       MODIFIEDBY                     ASC,
       MODIFIEDON                     ASC
);

CREATE INDEX IE_SAKAI_USER_EMAIL ON SAKAI_USER
(
       EMAIL                          ASC
);

--SET @time_zone = "+0:00";

INSERT INTO SAKAI_USER VALUES ('admin', '', 'Sakai', 'Administrator', '', 'ISMvKXpXpadDiUoOSoAf', 'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO SAKAI_USER VALUES ('postmaster', '', 'Sakai', 'Postmaster', '', 'ISMvKXpXpadDiUoOSoAf', 'postmaster', 'postmaster', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

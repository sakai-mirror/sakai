
-----------------------------------------------------------------------------
-- SAKAI_SITE_PROPERTY
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_SITE_PROPERTY;

CREATE TABLE SAKAI_SITE_PROPERTY (
       SITE_ID              VARCHAR (99) NOT NULL,
       NAME                 VARCHAR (99) NOT NULL,
       VALUE                LONGVARCHAR NULL,
       PRIMARY KEY (SITE_ID, NAME)
);

-----------------------------------------------------------------------------
-- SAKAI_SITE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_SITE;

CREATE TABLE SAKAI_SITE (
       SITE_ID              VARCHAR (99) NOT NULL,
       TITLE                VARCHAR (99) NULL,
       TYPE                 VARCHAR (99) NULL,
       SHORT_DESC           LONGVARCHAR NULL,
       DESCRIPTION          LONGVARCHAR NULL,
       ICON_URL             VARCHAR (255) NULL,
       INFO_URL             VARCHAR (255) NULL,
       SKIN                 VARCHAR (255) NULL,
       PUBLISHED            INTEGER NULL,
--                                   CHECK (PUBLISHED IN (0, 1)),
       JOINABLE             CHAR(1) NULL,
--                                   CHECK (JOINABLE IN (1, 0)),
       PUBVIEW              CHAR(1) NULL,
--                                   CHECK (PUBVIEW IN (1, 0)),
       JOIN_ROLE            VARCHAR (99) NULL,
       CREATEDBY            VARCHAR (99) NULL,
       MODIFIEDBY           VARCHAR (99) NULL,
       CREATEDON            DATETIME NULL,
       MODIFIEDON           DATETIME NULL,
       IS_SPECIAL           CHAR(1) NULL,
--                                   CHECK (IS_SPECIAL IN (1, 0)),
       IS_USER              CHAR(1) NULL,
--                                   CHECK (IS_USER IN (1, 0))
       PRIMARY KEY (SITE_ID)
);

ALTER TABLE SAKAI_SITE_PROPERTY
       ADD FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE (SITE_ID);

CREATE INDEX IE_SAKAI_SITE_CREATED ON SAKAI_SITE
(
       CREATEDBY                      ASC,
       CREATEDON                      ASC
);

CREATE INDEX IE_SAKAI_SITE_MODDED ON SAKAI_SITE
(
       MODIFIEDBY                     ASC,
       MODIFIEDON                     ASC
);

CREATE INDEX IE_SAKAI_SITE_FLAGS ON SAKAI_SITE
(
       SITE_ID,
       IS_SPECIAL,       
       IS_USER
);

-----------------------------------------------------------------------------
-- SAKAI_SITE_PAGE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_SITE_PAGE;

CREATE TABLE SAKAI_SITE_PAGE (
       PAGE_ID              VARCHAR (99) NOT NULL,
       SITE_ID              VARCHAR (99) NOT NULL,
       TITLE                VARCHAR (99) NULL,
       LAYOUT               CHAR(1) NULL,
       SITE_ORDER           INTEGER NOT NULL,
       PRIMARY KEY (PAGE_ID)
);

ALTER TABLE SAKAI_SITE_PAGE
       ADD FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE (SITE_ID);

-----------------------------------------------------------------------------
-- SAKAI_SITE_PAGE_PROPERTY
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_SITE_PAGE_PROPERTY;

CREATE TABLE SAKAI_SITE_PAGE_PROPERTY (
       SITE_ID              VARCHAR (99) NOT NULL,
       PAGE_ID              VARCHAR (99) NOT NULL,
       NAME                 VARCHAR (99) NOT NULL,
       VALUE                LONGVARCHAR NULL,
       PRIMARY KEY (PAGE_ID, NAME)
);

ALTER TABLE SAKAI_SITE_PAGE_PROPERTY
       ADD FOREIGN KEY (PAGE_ID)
                             REFERENCES SAKAI_SITE_PAGE (PAGE_ID);

ALTER TABLE SAKAI_SITE_PAGE_PROPERTY
       ADD FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE (SITE_ID);

-----------------------------------------------------------------------------
-- SAKAI_SITE_TOOL
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_SITE_TOOL;

CREATE TABLE SAKAI_SITE_TOOL (
       TOOL_ID              VARCHAR (99) NOT NULL,
       PAGE_ID              VARCHAR (99) NOT NULL,
       SITE_ID              VARCHAR (99) NOT NULL,
       REGISTRATION         VARCHAR (99) NOT NULL,
       PAGE_ORDER           INTEGER NOT NULL,
       TITLE                VARCHAR (99) NULL,
       LAYOUT_HINTS         VARCHAR (99) NULL,
       PRIMARY KEY (TOOL_ID)
);

ALTER TABLE SAKAI_SITE_TOOL
       ADD FOREIGN KEY (PAGE_ID)
                             REFERENCES SAKAI_SITE_PAGE (PAGE_ID);
ALTER TABLE SAKAI_SITE_TOOL
       ADD FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE (SITE_ID);

-----------------------------------------------------------------------------
-- SAKAI_SITE_TOOL_PROPERTY
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_SITE_TOOL_PROPERTY;

CREATE TABLE SAKAI_SITE_TOOL_PROPERTY (
       SITE_ID              VARCHAR (99) NOT NULL,
       TOOL_ID              VARCHAR (99) NOT NULL,
       NAME                 VARCHAR (99) NOT NULL,
       VALUE                LONGVARCHAR NULL,
       PRIMARY KEY (TOOL_ID, NAME)
);

ALTER TABLE SAKAI_SITE_TOOL_PROPERTY
       ADD FOREIGN KEY (TOOL_ID)
                             REFERENCES SAKAI_SITE_TOOL (TOOL_ID);

ALTER TABLE SAKAI_SITE_TOOL_PROPERTY
       ADD FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE (SITE_ID);

-----------------------------------------------------------------------------
-- SAKAI_SITE_USER
-- PERMISSION is -1 for write, 0 for read unpublished, 1 for read published
-- This table is a complete compilation of a user's site read/write capabilities
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_SITE_USER;

CREATE TABLE SAKAI_SITE_USER (
       SITE_ID              VARCHAR (99) NOT NULL,
       USER_ID              VARCHAR (99) NOT NULL,
       PERMISSION           INTEGER NOT NULL,
       PRIMARY KEY (SITE_ID, USER_ID)
);

ALTER TABLE SAKAI_SITE_USER
       ADD FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE (SITE_ID);



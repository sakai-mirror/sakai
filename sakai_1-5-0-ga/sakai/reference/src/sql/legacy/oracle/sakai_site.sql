
-----------------------------------------------------------------------------
-- SAKAI_SITE_PROPERTY
-----------------------------------------------------------------------------

DROP TABLE SAKAI_SITE_PROPERTY CASCADE CONSTRAINTS;

CREATE TABLE SAKAI_SITE_PROPERTY (
       SITE_ID              VARCHAR2(99) NOT NULL,
       NAME                 VARCHAR2(99) NOT NULL,
       VALUE                CLOB NULL
);


ALTER TABLE SAKAI_SITE_PROPERTY
       ADD  ( PRIMARY KEY (SITE_ID, NAME) ) ;

-----------------------------------------------------------------------------
-- SAKAI_SITE
-----------------------------------------------------------------------------

DROP TABLE SAKAI_SITE CASCADE CONSTRAINTS;

CREATE TABLE SAKAI_SITE (
       SITE_ID              VARCHAR2(99) NOT NULL,
       TITLE                VARCHAR2(99) NULL,
       TYPE                 VARCHAR2(99) NULL,
       SHORT_DESC           CLOB NULL,
       DESCRIPTION          CLOB NULL,
       ICON_URL             VARCHAR2(255) NULL,
       INFO_URL             VARCHAR2(255) NULL,
       SKIN                 VARCHAR2(255) NULL,
       PUBLISHED            INTEGER NULL
                                   CHECK (PUBLISHED IN (0, 1)),
       JOINABLE             CHAR(1) NULL
                                   CHECK (JOINABLE IN (1, 0)),
       PUBVIEW              CHAR(1) NULL
                                   CHECK (PUBVIEW IN (1, 0)),
       JOIN_ROLE            VARCHAR2(99) NULL,
       CREATEDBY            VARCHAR2(99) NULL,
       MODIFIEDBY           VARCHAR2(99) NULL,
       CREATEDON            TIMESTAMP NULL,
       MODIFIEDON           TIMESTAMP NULL,
       IS_SPECIAL           CHAR(1) NULL
                                   CHECK (IS_SPECIAL IN (1, 0)),
       IS_USER              CHAR(1) NULL
                                   CHECK (IS_USER IN (1, 0))
);

ALTER TABLE SAKAI_SITE
       ADD  ( PRIMARY KEY (SITE_ID) ) ;


ALTER TABLE SAKAI_SITE_PROPERTY
       ADD  ( FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE ) ;

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

DROP TABLE SAKAI_SITE_PAGE CASCADE CONSTRAINTS;

CREATE TABLE SAKAI_SITE_PAGE (
       PAGE_ID              VARCHAR2(99) NOT NULL,
       SITE_ID              VARCHAR2(99) NOT NULL,
       TITLE                VARCHAR2(99) NULL,
       LAYOUT               CHAR(1) NULL,
       SITE_ORDER           INTEGER NOT NULL
);

ALTER TABLE SAKAI_SITE_PAGE
       ADD  ( PRIMARY KEY (PAGE_ID) ) ;

ALTER TABLE SAKAI_SITE_PAGE
       ADD  ( FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE ) ;

CREATE INDEX IE_SAKAI_SITE_PAGE_SITE ON SAKAI_SITE_PAGE
(
       SITE_ID                       ASC
);

-----------------------------------------------------------------------------
-- SAKAI_SITE_PAGE_PROPERTY
-----------------------------------------------------------------------------

DROP TABLE SAKAI_SITE_PAGE_PROPERTY CASCADE CONSTRAINTS;

CREATE TABLE SAKAI_SITE_PAGE_PROPERTY (
       SITE_ID              VARCHAR2(99) NOT NULL,
       PAGE_ID              VARCHAR2(99) NOT NULL,
       NAME                 VARCHAR2(99) NOT NULL,
       VALUE                CLOB NULL
);

ALTER TABLE SAKAI_SITE_PAGE_PROPERTY
       ADD  ( PRIMARY KEY (PAGE_ID, NAME) ) ;

ALTER TABLE SAKAI_SITE_PAGE_PROPERTY
       ADD  ( FOREIGN KEY (PAGE_ID)
                             REFERENCES SAKAI_SITE_PAGE ) ;

ALTER TABLE SAKAI_SITE_PAGE_PROPERTY
       ADD  ( FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE ) ;

-----------------------------------------------------------------------------
-- SAKAI_SITE_TOOL
-----------------------------------------------------------------------------

DROP TABLE SAKAI_SITE_TOOL CASCADE CONSTRAINTS;

CREATE TABLE SAKAI_SITE_TOOL (
       TOOL_ID              VARCHAR2(99) NOT NULL,
       PAGE_ID              VARCHAR2(99) NOT NULL,
       SITE_ID              VARCHAR2(99) NOT NULL,
       REGISTRATION         VARCHAR2(99) NOT NULL,
       PAGE_ORDER           INTEGER NOT NULL,
       TITLE                VARCHAR2(99) NULL,
       LAYOUT_HINTS         VARCHAR2(99) NULL
);

ALTER TABLE SAKAI_SITE_TOOL
       ADD  ( PRIMARY KEY (TOOL_ID) ) ;

ALTER TABLE SAKAI_SITE_TOOL
       ADD  ( FOREIGN KEY (PAGE_ID)
                             REFERENCES SAKAI_SITE_PAGE ) ;
ALTER TABLE SAKAI_SITE_TOOL
       ADD  ( FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE ) ;

CREATE INDEX IE_SAKAI_SITE_TOOL_PAGE ON SAKAI_SITE_TOOL
(
       PAGE_ID                       ASC
);

CREATE INDEX IE_SAKAI_SITE_TOOL_SITE ON SAKAI_SITE_TOOL
(
       SITE_ID                       ASC
);

-----------------------------------------------------------------------------
-- SAKAI_SITE_TOOL_PROPERTY
-----------------------------------------------------------------------------

DROP TABLE SAKAI_SITE_TOOL_PROPERTY CASCADE CONSTRAINTS;

CREATE TABLE SAKAI_SITE_TOOL_PROPERTY (
       SITE_ID              VARCHAR2(99) NOT NULL,
       TOOL_ID              VARCHAR2(99) NOT NULL,
       NAME                 VARCHAR2(99) NOT NULL,
       VALUE                CLOB NULL
);

ALTER TABLE SAKAI_SITE_TOOL_PROPERTY
       ADD  ( PRIMARY KEY (TOOL_ID, NAME) ) ;

ALTER TABLE SAKAI_SITE_TOOL_PROPERTY
       ADD  ( FOREIGN KEY (TOOL_ID)
                             REFERENCES SAKAI_SITE_TOOL ) ;

ALTER TABLE SAKAI_SITE_TOOL_PROPERTY
       ADD  ( FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE ) ;

-----------------------------------------------------------------------------
-- SAKAI_SITE_USER
-- PERMISSION is -1 for write, 0 for read unpublished, 1 for read published
-- This table is a complete compilation of a user's site read/write capabilities
-----------------------------------------------------------------------------

DROP TABLE SAKAI_SITE_USER CASCADE CONSTRAINTS;

CREATE TABLE SAKAI_SITE_USER (
       SITE_ID              VARCHAR2(99) NOT NULL,
       USER_ID              VARCHAR2(99) NOT NULL,
       PERMISSION           INTEGER NOT NULL
);

ALTER TABLE SAKAI_SITE_USER
       ADD  ( PRIMARY KEY (SITE_ID, USER_ID) ) ;

CREATE INDEX IE_SAKAI_SITE_USER_USER ON SAKAI_SITE_USER
(
       USER_ID                       ASC
);

ALTER TABLE SAKAI_SITE_USER
       ADD  ( FOREIGN KEY (SITE_ID)
                             REFERENCES SAKAI_SITE ) ;


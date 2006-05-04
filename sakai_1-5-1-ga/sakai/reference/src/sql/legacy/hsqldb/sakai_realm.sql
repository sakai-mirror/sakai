-----------------------------------------------------------------------------
-- SAKAI_REALM
-- Note: REALM_ID is the old "resource reference" string id for the realm
--       _KEY is the "internal" integer id used to crossreference the other tables
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_REALM;

CREATE TABLE SAKAI_REALM (
       REALM_KEY            INTEGER NOT NULL,
       REALM_ID             VARCHAR (200) NOT NULL,
       PROVIDER_ID          VARCHAR (1024) NULL,
       MAINTAIN_ROLE        INTEGER NULL,
       CREATEDBY            VARCHAR (99) NULL,
       MODIFIEDBY           VARCHAR (99) NULL,
       CREATEDON            DATETIME NULL,
       MODIFIEDON           DATETIME NULL,
       PRIMARY KEY(REALM_KEY),
       CONSTRAINT AK_SAKAI_REALM_ID UNIQUE (REALM_ID)
);

CREATE INDEX IE_SAKAI_REALM_CREATED ON SAKAI_REALM
(
       CREATEDBY                      ASC,
       CREATEDON                      ASC
);

CREATE INDEX IE_SAKAI_REALM_MODDED ON SAKAI_REALM
(
       MODIFIEDBY                     ASC,
       MODIFIEDON                     ASC
);

CREATE SEQUENCE SAKAI_REALM_SEQ;

-----------------------------------------------------------------------------
-- SAKAI_REALM_PROPERTY
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_REALM_PROPERTY;

CREATE TABLE SAKAI_REALM_PROPERTY (
       REALM_KEY            INTEGER NOT NULL,
       NAME                 VARCHAR (99) NOT NULL,
       VALUE                LONGVARCHAR NULL,
       PRIMARY KEY (REALM_KEY, NAME)
);

CREATE INDEX FK_SAKAI_REALM_PROPERTY ON SAKAI_REALM_PROPERTY
(
       REALM_KEY                       ASC
);

-----------------------------------------------------------------------------
-- SAKAI_REALM_ROLE
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_REALM_ROLE;

CREATE TABLE SAKAI_REALM_ROLE (
       ROLE_KEY             INTEGER NOT NULL,
       ROLE_NAME            VARCHAR (99) NOT NULL,
       PRIMARY KEY (ROLE_KEY),
       CONSTRAINT IE_SAKAI_REALM_ROLE_NAME UNIQUE (ROLE_NAME)
);

CREATE SEQUENCE SAKAI_REALM_ROLE_SEQ;

-----------------------------------------------------------------------------
-- SAKAI_REALM_ROLE_DESC
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_REALM_ROLE_DESC;

CREATE TABLE SAKAI_REALM_ROLE_DESC (
       REALM_KEY            INTEGER NOT NULL,
       ROLE_KEY             INTEGER NOT NULL,
       DESCRIPTION          LONGVARCHAR NULL,
       PRIMARY KEY (REALM_KEY, ROLE_KEY)
);

CREATE INDEX FK_SAKAI_REALM_ROLE_DESC_REALM ON SAKAI_REALM_ROLE_DESC
(
       REALM_KEY                      ASC
);

-----------------------------------------------------------------------------
-- SAKAI_REALM_FUNCTION
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_REALM_FUNCTION;

CREATE TABLE SAKAI_REALM_FUNCTION (
       FUNCTION_KEY         INTEGER NOT NULL,
       FUNCTION_NAME        VARCHAR (99) NOT NULL,
       PRIMARY KEY (FUNCTION_KEY),
       CONSTRAINT IE_SAKAI_REALM_FUNCTION_NAME UNIQUE (FUNCTION_NAME)
);

CREATE INDEX SAKAI_REALM_FUNCTION_KN ON SAKAI_REALM_FUNCTION
(
	FUNCTION_KEY,
	FUNCTION_NAME
);

CREATE SEQUENCE SAKAI_REALM_FUNCTION_SEQ;

-----------------------------------------------------------------------------
-- SAKAI_REALM_PROVIDER
-- provider id here is individual ids, where in the main table it may be
-- a single compound id
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_REALM_PROVIDER;

CREATE TABLE SAKAI_REALM_PROVIDER (
       REALM_KEY            INTEGER NOT NULL,
       PROVIDER_ID          VARCHAR (200) NOT NULL,
       PRIMARY KEY (REALM_KEY, PROVIDER_ID)
);

CREATE INDEX FK_SAKAI_REALM_PROVIDER ON SAKAI_REALM_PROVIDER
(
       REALM_KEY                       ASC
);

CREATE INDEX IE_SAKAI_REALM_PROVIDER_ID ON SAKAI_REALM_PROVIDER
(
       PROVIDER_ID                       ASC
);

-----------------------------------------------------------------------------
-- SAKAI_REALM_RL_FN
-- role function
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_REALM_RL_FN;

CREATE TABLE SAKAI_REALM_RL_FN (
       REALM_KEY            INTEGER NOT NULL,
       ROLE_KEY             INTEGER NOT NULL,
       FUNCTION_KEY         INTEGER NOT NULL,
       PRIMARY KEY (REALM_KEY, ROLE_KEY, FUNCTION_KEY)
);

CREATE INDEX FK_SAKAI_REALM_RL_FN_REALM ON SAKAI_REALM_RL_FN
(
       REALM_KEY                      ASC
);

CREATE INDEX FK_SAKAI_REALM_RL_FN_FUNC ON SAKAI_REALM_RL_FN
(
       FUNCTION_KEY                   ASC
);

CREATE INDEX FJ_SAKAI_REALM_RL_FN_ROLE ON SAKAI_REALM_RL_FN
(
       ROLE_KEY                       ASC
);

-----------------------------------------------------------------------------
-- SAKAI_REALM_RL_GR
-- role grant
-----------------------------------------------------------------------------

DROP TABLE IF EXISTS SAKAI_REALM_RL_GR;

CREATE TABLE SAKAI_REALM_RL_GR (
       REALM_KEY            INTEGER NOT NULL,
       USER_ID              VARCHAR (99) NOT NULL,
       ROLE_KEY             INTEGER NOT NULL,
       ACTIVE               CHAR(1) NULL,
--                                   CHECK (ACTIVE IN (1, 0)),
       PROVIDED             CHAR(1) NULL,
--                                   CHECK (PROVIDED IN (1, 0))
       PRIMARY KEY (REALM_KEY, USER_ID)
);

CREATE INDEX FK_SAKAI_REALM_RL_GR_REALM ON SAKAI_REALM_RL_GR
(
       REALM_KEY                      ASC
);

CREATE INDEX FK_SAKAI_REALM_RL_GR_ROLE ON SAKAI_REALM_RL_GR
(
       ROLE_KEY                      ASC
);

CREATE INDEX IE_SAKAI_REALM_RL_GR_ACT ON SAKAI_REALM_RL_GR
(
       ACTIVE                       ASC
);

CREATE INDEX IE_SAKAI_REALM_RL_GR_USR ON SAKAI_REALM_RL_GR
(
       USER_ID                       ASC
);

CREATE INDEX IE_SAKAI_REALM_RL_GR_PRV ON SAKAI_REALM_RL_GR
(
       PROVIDED                       ASC
);

CREATE INDEX SAKAI_REALM_RL_GR_RAU ON SAKAI_REALM_RL_GR
(
	ROLE_KEY,
	ACTIVE,
	USER_ID
);

-----------------------------------------------------------------------------
-- FOREIGN KEYS
-----------------------------------------------------------------------------

ALTER TABLE SAKAI_REALM
       ADD FOREIGN KEY (MAINTAIN_ROLE)
                             REFERENCES SAKAI_REALM_ROLE (ROLE_KEY);

ALTER TABLE SAKAI_REALM_PROPERTY
       ADD FOREIGN KEY (REALM_KEY)
                             REFERENCES SAKAI_REALM (REALM_KEY);

ALTER TABLE SAKAI_REALM_PROVIDER
       ADD FOREIGN KEY (REALM_KEY)
                             REFERENCES SAKAI_REALM (REALM_KEY);

ALTER TABLE SAKAI_REALM_RL_FN
       ADD FOREIGN KEY (REALM_KEY)
                             REFERENCES SAKAI_REALM (REALM_KEY);

ALTER TABLE SAKAI_REALM_RL_FN
       ADD FOREIGN KEY (ROLE_KEY)
                             REFERENCES SAKAI_REALM_ROLE (ROLE_KEY);

ALTER TABLE SAKAI_REALM_RL_FN
       ADD FOREIGN KEY (FUNCTION_KEY)
                             REFERENCES SAKAI_REALM_FUNCTION (FUNCTION_KEY);

ALTER TABLE SAKAI_REALM_ROLE_DESC
       ADD FOREIGN KEY (REALM_KEY)
                             REFERENCES SAKAI_REALM (REALM_KEY);

ALTER TABLE SAKAI_REALM_ROLE_DESC
       ADD FOREIGN KEY (ROLE_KEY)
                             REFERENCES SAKAI_REALM_ROLE (ROLE_KEY);

ALTER TABLE SAKAI_REALM_RL_GR
       ADD FOREIGN KEY (REALM_KEY)
                             REFERENCES SAKAI_REALM (REALM_KEY);

ALTER TABLE SAKAI_REALM_RL_GR
       ADD FOREIGN KEY (ROLE_KEY)
                             REFERENCES SAKAI_REALM_ROLE (ROLE_KEY);
-----------------------------------------------------------------------------
-- SAKAI_REALM
-- Note: REALM_ID is the old "resource reference" string id for the realm
--       _KEY is the "internal" integer id used to crossreference the other tables
-----------------------------------------------------------------------------

CREATE TABLE SAKAI_REALM (
       REALM_KEY            INTEGER NOT NULL AUTO_INCREMENT,
       REALM_ID             VARCHAR (200) NOT NULL,
       PROVIDER_ID          VARCHAR (1024) NULL,
       MAINTAIN_ROLE        INTEGER NULL,
       CREATEDBY            VARCHAR (99) NULL,
       MODIFIEDBY           VARCHAR (99) NULL,
       CREATEDON            DATETIME NULL,
       MODIFIEDON           DATETIME NULL,
       PRIMARY KEY(REALM_KEY)
);

CREATE UNIQUE INDEX AK_SAKAI_REALM_ID ON SAKAI_REALM
(
       REALM_ID                       ASC
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

-----------------------------------------------------------------------------
-- SAKAI_REALM_PROPERTY
-----------------------------------------------------------------------------

CREATE TABLE SAKAI_REALM_PROPERTY (
       REALM_KEY            INTEGER NOT NULL,
       NAME                 VARCHAR (99) NOT NULL,
       VALUE                MEDIUMTEXT NULL
);

ALTER TABLE SAKAI_REALM_PROPERTY
       ADD  ( PRIMARY KEY (REALM_KEY, NAME) ) ;

CREATE INDEX FK_SAKAI_REALM_PROPERTY ON SAKAI_REALM_PROPERTY
(
       REALM_KEY                       ASC
);

-----------------------------------------------------------------------------
-- SAKAI_REALM_ROLE
-----------------------------------------------------------------------------

CREATE TABLE SAKAI_REALM_ROLE (
       ROLE_KEY             INTEGER NOT NULL AUTO_INCREMENT,
       ROLE_NAME            VARCHAR (99) NOT NULL,
       PRIMARY KEY (ROLE_KEY)
);

CREATE UNIQUE INDEX IE_SAKAI_REALM_ROLE_NAME ON SAKAI_REALM_ROLE
(
       ROLE_NAME                       ASC
);

-----------------------------------------------------------------------------
-- SAKAI_REALM_ROLE_DESC
-----------------------------------------------------------------------------

CREATE TABLE SAKAI_REALM_ROLE_DESC (
       REALM_KEY            INTEGER NOT NULL,
       ROLE_KEY             INTEGER NOT NULL,
       DESCRIPTION          MEDIUMTEXT NULL
);

ALTER TABLE SAKAI_REALM_ROLE_DESC
       ADD  ( PRIMARY KEY (REALM_KEY, ROLE_KEY) ) ;

CREATE INDEX FK_SAKAI_REALM_ROLE_DESC_REALM ON SAKAI_REALM_ROLE_DESC
(
       REALM_KEY                      ASC
);

-----------------------------------------------------------------------------
-- SAKAI_REALM_FUNCTION
-----------------------------------------------------------------------------

CREATE TABLE SAKAI_REALM_FUNCTION (
       FUNCTION_KEY         INTEGER NOT NULL AUTO_INCREMENT,
       FUNCTION_NAME        VARCHAR (99) NOT NULL,
       PRIMARY KEY (FUNCTION_KEY)
);

CREATE UNIQUE INDEX IE_SAKAI_REALM_FUNCTION_NAME ON SAKAI_REALM_FUNCTION
(
       FUNCTION_NAME                       ASC
);

CREATE INDEX SAKAI_REALM_FUNCTION_KN ON SAKAI_REALM_FUNCTION
(
	FUNCTION_KEY,
	FUNCTION_NAME
);

-----------------------------------------------------------------------------
-- SAKAI_REALM_PROVIDER
-- provider id here is individual ids, where in the main table it may be
-- a single compound id
-----------------------------------------------------------------------------

CREATE TABLE SAKAI_REALM_PROVIDER (
       REALM_KEY            INTEGER NOT NULL,
       PROVIDER_ID          VARCHAR (200) NOT NULL
);

ALTER TABLE SAKAI_REALM_PROVIDER
       ADD  ( PRIMARY KEY (REALM_KEY, PROVIDER_ID) ) ;

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

CREATE TABLE SAKAI_REALM_RL_FN (
       REALM_KEY            INTEGER NOT NULL,
       ROLE_KEY             INTEGER NOT NULL,
       FUNCTION_KEY         INTEGER NOT NULL
);

ALTER TABLE SAKAI_REALM_RL_FN
       ADD  ( PRIMARY KEY (REALM_KEY, ROLE_KEY, FUNCTION_KEY) ) ;

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

CREATE TABLE SAKAI_REALM_RL_GR (
       REALM_KEY            INTEGER NOT NULL,
       USER_ID              VARCHAR (99) NOT NULL,
       ROLE_KEY             INTEGER NOT NULL,
       ACTIVE               CHAR(1) NULL
                                   CHECK (ACTIVE IN (1, 0)),
       PROVIDED             CHAR(1) NULL
                                   CHECK (PROVIDED IN (1, 0))
);

ALTER TABLE SAKAI_REALM_RL_GR
       ADD  ( PRIMARY KEY (REALM_KEY, USER_ID) ) ;

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
       ADD  ( FOREIGN KEY (MAINTAIN_ROLE)
                             REFERENCES SAKAI_REALM_ROLE (ROLE_KEY) ) ;

ALTER TABLE SAKAI_REALM_PROPERTY
       ADD  ( FOREIGN KEY (REALM_KEY)
                             REFERENCES SAKAI_REALM (REALM_KEY) ) ;

ALTER TABLE SAKAI_REALM_PROVIDER
       ADD  ( FOREIGN KEY (REALM_KEY)
                             REFERENCES SAKAI_REALM (REALM_KEY) ) ;

ALTER TABLE SAKAI_REALM_RL_FN
       ADD  ( FOREIGN KEY (REALM_KEY)
                             REFERENCES SAKAI_REALM (REALM_KEY) ) ;

ALTER TABLE SAKAI_REALM_RL_FN
       ADD  ( FOREIGN KEY (ROLE_KEY)
                             REFERENCES SAKAI_REALM_ROLE (ROLE_KEY) ) ;

ALTER TABLE SAKAI_REALM_RL_FN
       ADD  ( FOREIGN KEY (FUNCTION_KEY)
                             REFERENCES SAKAI_REALM_FUNCTION (FUNCTION_KEY) ) ;

ALTER TABLE SAKAI_REALM_ROLE_DESC
       ADD  ( FOREIGN KEY (REALM_KEY)
                             REFERENCES SAKAI_REALM (REALM_KEY) ) ;

ALTER TABLE SAKAI_REALM_ROLE_DESC
       ADD  ( FOREIGN KEY (ROLE_KEY)
                             REFERENCES SAKAI_REALM_ROLE (ROLE_KEY) ) ;

ALTER TABLE SAKAI_REALM_RL_GR
       ADD  ( FOREIGN KEY (REALM_KEY)
                             REFERENCES SAKAI_REALM (REALM_KEY) ) ;

ALTER TABLE SAKAI_REALM_RL_GR
       ADD  ( FOREIGN KEY (ROLE_KEY)
                             REFERENCES SAKAI_REALM_ROLE (ROLE_KEY) ) ;

INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, '.anon');
INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, '.auth');
INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, 'maintain');
INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, 'access');
INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, 'pubview');
INSERT INTO SAKAI_REALM_ROLE VALUES (DEFAULT, 'admin');

INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.delete.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.delete.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.read.drafts');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.revise.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'annc.revise.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'asn.delete');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'asn.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'asn.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'asn.revise');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'asn.submit');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'calendar.delete');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'calendar.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'calendar.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'calendar.revise');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.delete.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.delete.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.revise.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'chat.revise.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'content.delete');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'content.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'content.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'content.revise');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.dis.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.dis.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.dis.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.dis.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.path.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.path.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.path.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.path.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.status.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.status.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.status.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.status.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.step.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.step.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.step.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dis.step.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.delete.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.delete.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.new.topic');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.read.drafts');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.revise.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'disc.revise.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'dropbox.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.delete.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.delete.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.read');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.revise.any');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.revise.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'mail.new');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'prefs.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'prefs.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'prefs.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'realm.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'realm.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'realm.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'realm.upd.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.add.usersite');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.del');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.upd');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.visit');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'site.visit.unp');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'user.add');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'user.upd.own');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'gradebook.access');
INSERT INTO SAKAI_REALM_FUNCTION VALUES (DEFAULT, 'gradebook.maintain');

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!site.helper', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.helper'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), 
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.helper'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), 
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.del'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!site.user', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new.topic'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit.unp'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.user'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.upd'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!user.template', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add.usersite'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.del'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!user.template.guest', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add.usersite'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.guest'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.del'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!user.template.maintain', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add.usersite'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.maintain'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!user.template.registered', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add.usersite'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.registered'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!user.template.sample', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add.usersite'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'user.upd.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'prefs.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!user.template.sample'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.add'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!site.template', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.submit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dropbox.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'gradebook.access'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new.topic'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit.unp'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!site.template'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'gradebook.maintain'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/content/public/', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/public/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/public/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/content/attachment/', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/attachment/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/attachment/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/attachment/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/attachment/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/content/attachment/'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.revise'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/announcement/channel/!site/motd', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/announcement/channel/!site/motd'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/announcement/channel/!site/motd'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '!pubview', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '!pubview'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'pubview'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/site/!gateway', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!gateway'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!gateway'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/site/!error', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!error'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!error'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
	
INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/site/!urlError', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!urlError'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!urlError'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/site/mercury', '', NULL, 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.submit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dropbox.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'access'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'gradebook.access'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'annc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'asn.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'calendar.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'chat.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.revise'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'content.delete'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.new.topic'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.read.drafts'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'disc.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.new'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.revise.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.own'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'mail.delete.any'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'realm.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.visit.unp'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.path.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.upd'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.status.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.dis.del'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.add'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'dis.step.read'));
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'gradebook.maintain'));
INSERT INTO SAKAI_REALM_RL_GR VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/mercury'),
	'admin',(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'maintain'), '1', '0');

INSERT INTO SAKAI_REALM VALUES (DEFAULT, '/site/!admin', '', (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'admin'), 'admin', 'admin', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO SAKAI_REALM_RL_FN VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'admin'),
	(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = 'site.upd'));
INSERT INTO SAKAI_REALM_RL_GR VALUES((select REALM_KEY from SAKAI_REALM where REALM_ID = '/site/!admin'),
	'admin',(select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = 'admin'), '1', '0');
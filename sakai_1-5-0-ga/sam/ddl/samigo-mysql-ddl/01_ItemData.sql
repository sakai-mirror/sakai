DROP TABLE ITEM;

CREATE TABLE ITEM (
  ITEMID          INTEGER        NOT NULL ,
  SECTIONID          INTEGER    ,
  ITEMIDSTRING     VARCHAR(36) ,
  SEQUENCE        INTEGER         ,
  DURATION        INTEGER         ,
  TRIESALLOWED        INTEGER         ,
  INSTRUCTION      LONG VARCHAR ,
  DESCRIPTION      LONG VARCHAR ,
  TYPEID           VARCHAR(36)           NOT NULL ,
  GRADE            VARCHAR(80) ,
  SCORE            FLOAT,
  HINT             LONG VARCHAR ,
  HASRATIONALE     VARCHAR(1)          ,
  STATUS          INTEGER        NOT NULL ,
  CREATEDBY        VARCHAR(36)           NOT NULL ,
  CREATEDDATE      DATETIME          NOT NULL ,
  LASTMODIFIEDBY    VARCHAR(36)           NOT NULL ,
  LASTMODIFIEDDATE  DATETIME          NOT NULL ,
  CONSTRAINT PK_ITEM PRIMARY KEY (ITEMID)
);


DROP TABLE ITEMTEXT CASCADE CONSTRAINTS;

CREATE TABLE ITEMTEXT (
  ITEMTEXTID INTEGER      NOT NULL   ,
  ITEMID     INTEGER         NOT NULL,
  SEQUENCE   INTEGER          NOT NULL ,
  TEXT        LONG VARCHAR ,
  CONSTRAINT PK_ITEMTEXT PRIMARY KEY (ITEMTEXTID),
  CONSTRAINT FK_ITEMTEXT_ITEMID FOREIGN KEY (ITEMID) REFERENCES ITEM(ITEMID)
);

DROP TABLE ITEMMETADATA CASCADE CONSTRAINTS;

CREATE TABLE ITEMMETADATA (
  ITEMMETADATAID INTEGER       NOT NULL ,
  ITEMID          INTEGER         NOT NULL,
  LABEL            VARCHAR(255)  NOT NULL,
  ENTRY            VARCHAR(255) ,
  CONSTRAINT PK_ITEMMETADATA PRIMARY KEY (ITEMMETADATAID),
  CONSTRAINT FK_ITEMMETADATA_ITEMID FOREIGN KEY (ITEMID) REFERENCES ITEM(ITEMID)
);

DROP TABLE ANSWER CASCADE CONSTRAINTS;

CREATE TABLE ANSWER (
  ANSWERID   INTEGER          NOT NULL ,
  ITEMTEXTID INTEGER          NOT NULL ,
  ITEMID     INTEGER          NOT NULL ,
  TEXT        LONG VARCHAR,
  SEQUENCE   INTEGER         ,
  LABEL       VARCHAR(20)  ,
  ISCORRECT   VARCHAR(1) ,
  GRADE            VARCHAR(80) ,
  SCORE            FLOAT ,
  CONSTRAINT PK_ANSWER PRIMARY KEY (ANSWERID),
  CONSTRAINT FK_ANSWER_ITEMTEXTID FOREIGN KEY (ITEMTEXTID) REFERENCES ITEMTEXT(ITEMTEXTID),
  CONSTRAINT FK_ANSWER_ITEMID FOREIGN KEY (ITEMID) REFERENCES ITEM(ITEMID)
);

DROP TABLE ITEMFEEDBACK CASCADE CONSTRAINTS;

CREATE TABLE ITEMFEEDBACK (
  ITEMFEEDBACKID    INTEGER          NOT NULL ,
  ITEMID            INTEGER          NOT NULL ,
  TYPEID    VARCHAR(36) NOT NULL,
  TEXT  LONG VARCHAR ,
  CONSTRAINT PK_ITEMFEEDBACK PRIMARY KEY (ITEMFEEDBACKID),
  CONSTRAINT FK_ITEMFEEDBACK_ITEMID FOREIGN KEY (ITEMID) REFERENCES ITEM(ITEMID)
);

DROP TABLE ANSWERFEEDBACK CASCADE CONSTRAINTS;

CREATE TABLE ANSWERFEEDBACK (
  ANSWERFEEDBACKID    INTEGER          NOT NULL ,
  ANSWERID            INTEGER          NOT NULL ,
  TYPEID    VARCHAR(36) NOT NULL,
  TEXT  LONG VARCHAR ,
  CONSTRAINT PK_ANSWERFEEDBACK PRIMARY KEY (ANSWERFEEDBACKID),
  CONSTRAINT FK_ANSWERFEEDBACK_ANSWERID FOREIGN KEY (ANSWERID) REFERENCES ANSWER(ANSWERID)
);

